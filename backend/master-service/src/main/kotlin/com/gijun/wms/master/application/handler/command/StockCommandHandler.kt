package com.gijun.wms.master.application.handler.command

import com.gijun.wms.master.application.dto.command.RecordAdjustmentCommand
import com.gijun.wms.master.application.dto.command.RecordReceiptCommand
import com.gijun.wms.master.application.dto.command.RecordTransferCommand
import com.gijun.wms.master.application.dto.result.StockMovementResult
import com.gijun.wms.master.application.dto.result.TransferResult
import com.gijun.wms.master.application.port.`in`.command.RecordAdjustmentCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.RecordReceiptCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.RecordTransferCommandUseCase
import com.gijun.wms.master.application.port.out.message.PublishStockMovementPort
import com.gijun.wms.master.application.port.out.persistence.item.ItemQueryPersistencePort
import com.gijun.wms.master.application.port.out.persistence.location.LocationQueryPersistencePort
import com.gijun.wms.master.application.port.out.persistence.stock.StockCommandPersistencePort
import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.item.exception.ItemException
import com.gijun.wms.master.domain.location.exception.LocationException
import com.gijun.wms.master.domain.stock.StockLedger
import com.gijun.wms.master.domain.stock.StockMovementModel
import com.gijun.wms.master.domain.stock.exception.StockException
import com.gijun.wms.shared.event.StockMovementEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 재고원장 커맨드 핸들러 — 마스터 검증 → 잔고 행 락 → [StockLedger] 적용 → 기록 순서의 배관만 담당.
 * 부호 정책·불변식(qty >= 0, seq 증가, TRANSFER 분해)은 전부 도메인이 집행한다.
 *
 * 마스터 검증 정책: 존재하지 않으면 404, **유입(+) 방향의 대상만 ACTIVE 를 요구**한다 —
 * 비활성 SKU/로케이션도 남은 재고의 반출·조정은 가능해야 하기 때문.
 */
@Service
class StockCommandHandler(
    private val stockCommandPersistencePort: StockCommandPersistencePort,
    private val itemQueryPersistencePort: ItemQueryPersistencePort,
    private val locationQueryPersistencePort: LocationQueryPersistencePort,
    private val publishStockMovementPort: PublishStockMovementPort,
) : RecordReceiptCommandUseCase, RecordTransferCommandUseCase, RecordAdjustmentCommandUseCase {

    @Transactional
    override fun receive(command: RecordReceiptCommand): StockMovementResult {
        validateSku(command.skuId, requireActive = true)
        validateLocation(command.locationId, requireActive = true)

        val balance = stockCommandPersistencePort.ensureAndLock(command.skuId, command.locationId)
        val applied = StockLedger.receive(balance, command.qty, command.refType, command.refId)
        val saved = stockCommandPersistencePort.persist(applied)
        publishStockMovementPort.publish(saved.toEvent())
        return StockMovementResult.from(saved, applied.balance.qty)
    }

    @Transactional
    override fun transfer(command: RecordTransferCommand): TransferResult {
        validateSku(command.skuId, requireActive = false)
        validateLocation(command.fromLocationId, requireActive = false)
        validateLocation(command.toLocationId, requireActive = true)   // 유입 지점만 ACTIVE 요구

        // 데드락 방지 — 두 잔고 행을 locationId 오름차순으로 잠근다(A→B 와 B→A 동시 요청 대비).
        // 정렬은 락 획득 순서에만 적용하고, 도메인에는 원래 방향(from/to)대로 넘긴다.
        val locked = listOf(command.fromLocationId, command.toLocationId).sorted()
            .associateWith { stockCommandPersistencePort.ensureAndLock(command.skuId, it) }

        val applied = StockLedger.transfer(
            from = locked.getValue(command.fromLocationId),
            to = locked.getValue(command.toLocationId),
            qty = command.qty,
            refType = command.refType,
            refId = command.refId,
        )
        val outbound = stockCommandPersistencePort.persist(applied.outbound)
        val inbound = stockCommandPersistencePort.persist(applied.inbound)
        publishStockMovementPort.publish(outbound.toEvent())
        publishStockMovementPort.publish(inbound.toEvent())
        return TransferResult(
            outbound = StockMovementResult.from(outbound, applied.outbound.balance.qty),
            inbound = StockMovementResult.from(inbound, applied.inbound.balance.qty),
        )
    }

    @Transactional
    override fun adjust(command: RecordAdjustmentCommand): StockMovementResult {
        // 조정은 실사 현실의 반영이므로 존재만 검증하고 상태는 불문.
        validateSku(command.skuId, requireActive = false)
        validateLocation(command.locationId, requireActive = false)

        val balance = stockCommandPersistencePort.ensureAndLock(command.skuId, command.locationId)
        val applied = StockLedger.adjust(balance, command.qtyDelta, command.refType, command.refId)
        val saved = stockCommandPersistencePort.persist(applied)
        publishStockMovementPort.publish(saved.toEvent())
        return StockMovementResult.from(saved, applied.balance.qty)
    }

    private fun validateSku(skuId: Long, requireActive: Boolean) {
        val sku = itemQueryPersistencePort.findSkuById(skuId)
            ?: throw ItemException.SkuNotFoundException("id=$skuId")
        if (requireActive && sku.status != ItemStatus.ACTIVE) {
            throw StockException.InactiveTargetException("SKU $skuId")
        }
    }

    private fun validateLocation(locationId: Long, requireActive: Boolean) {
        val location = locationQueryPersistencePort.findById(locationId)
            ?: throw LocationException.LocationNotFoundException(locationId)
        if (requireActive && location.status != LocationStatus.ACTIVE) {
            throw StockException.InactiveTargetException("로케이션 $locationId")
        }
    }

    /** 저장된 movement(id 확정) → 척추 이벤트. movementId = 원장 PK 가 컨슈머의 멱등 키. */
    private fun StockMovementModel.toEvent(): StockMovementEvent = StockMovementEvent(
        movementId = requireNotNull(id).toString(),
        skuId = skuId,
        locationId = locationId,
        type = type,
        qty = qty,
        refType = refType,
        refId = refId,
        seq = seq,
        occurredAt = occurredAt,
    )
}
