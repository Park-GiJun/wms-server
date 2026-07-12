package com.gijun.wms.master.application.dto.result

import com.gijun.wms.master.domain.stock.StockMovementModel
import com.gijun.wms.shared.event.MovementType
import java.time.Instant

/** 기록된 원장 레코드 + 적용 후 잔고 — 클라이언트가 재조회 없이 결과를 알 수 있게. */
data class StockMovementResult(
    val id: Long,
    val skuId: Long,
    val locationId: Long,
    val type: MovementType,
    val qty: Long,
    val refType: String?,
    val refId: String?,
    val seq: Long,
    val occurredAt: Instant,
    val balanceQty: Long,
) {
    companion object {
        fun from(movement: StockMovementModel, balanceQty: Long): StockMovementResult = StockMovementResult(
            id = requireNotNull(movement.id) { "저장된 movement 는 id 가 있어야 한다" },
            skuId = movement.skuId,
            locationId = movement.locationId,
            type = movement.type,
            qty = movement.qty,
            refType = movement.refType,
            refId = movement.refId,
            seq = movement.seq,
            occurredAt = movement.occurredAt,
            balanceQty = balanceQty,
        )
    }
}

/** transfer 결과 — 출발지(-qty)/도착지(+qty) 두 건. */
data class TransferResult(
    val outbound: StockMovementResult,
    val inbound: StockMovementResult,
)
