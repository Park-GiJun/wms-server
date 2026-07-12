package com.gijun.wms.master.domain.stock

import com.gijun.wms.master.domain.stock.exception.StockException
import com.gijun.wms.shared.event.MovementType

/**
 * 재고원장 도메인 서비스 — 이동 종류별 부호 정책과 TRANSFER 분해 규칙.
 * 잔고 불변식(qty >= 0, seq 단조 증가)은 [StockBalanceModel.apply] 가 집행한다.
 */
object StockLedger {

    /** 입고 — 수량은 양수만. */
    fun receive(balance: StockBalanceModel, qty: Long, refType: String? = null, refId: String? = null): StockApplication {
        requirePositive(qty)
        return balance.apply(MovementType.RECEIPT, qty, refType, refId)
    }

    /** 조정(실사 차이 등) — 유일하게 양방향(±) 허용. 감소는 가용 잔고까지만(음수 잔고 불허). */
    fun adjust(balance: StockBalanceModel, qtyDelta: Long, refType: String? = null, refId: String? = null): StockApplication =
        balance.apply(MovementType.ADJUSTMENT, qtyDelta, refType, refId)

    /**
     * 로케이션 이동 — movement 2건으로 분해한다(출발지 -qty + 도착지 +qty, 같은 ref 공유).
     * 호출부(영속 계층)는 데드락 방지를 위해 두 잔고 행을 (skuId, locationId) 오름차순으로 잠근다.
     */
    fun transfer(
        from: StockBalanceModel,
        to: StockBalanceModel,
        qty: Long,
        refType: String? = null,
        refId: String? = null,
    ): TransferApplication {
        requirePositive(qty)
        require(from.skuId == to.skuId) { "transfer 는 같은 SKU 의 잔고 간에만 가능합니다." }
        if (from.locationId == to.locationId) {
            throw StockException.SameLocationTransferException(from.locationId)
        }
        return TransferApplication(
            outbound = from.apply(MovementType.TRANSFER, -qty, refType, refId),
            inbound = to.apply(MovementType.TRANSFER, qty, refType, refId),
        )
    }

    private fun requirePositive(qty: Long) {
        if (qty <= 0) {
            throw StockException.InvalidQuantityException("수량은 양수여야 합니다: $qty")
        }
    }
}

/** [StockLedger.transfer] 의 결과 — 출발지(-qty)/도착지(+qty) 두 건의 적용. */
data class TransferApplication(
    val outbound: StockApplication,
    val inbound: StockApplication,
)
