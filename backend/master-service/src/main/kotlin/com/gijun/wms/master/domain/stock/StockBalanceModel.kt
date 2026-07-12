package com.gijun.wms.master.domain.stock

import com.gijun.wms.master.domain.stock.exception.StockException
import com.gijun.wms.shared.event.MovementType
import java.time.Instant

/**
 * (skuId, locationId) 현재 잔고 — 원장의 파생 상태이자 qty >= 0 불변식의 집행 지점.
 * 이동 반영은 [apply] 하나로만 이루어진다(단일 라이터 관문). 영속 계층은 이 행을
 * SELECT ... FOR UPDATE 로 잠근 뒤 apply 결과(잔고 갱신 + movement)를 한 트랜잭션에 기록한다.
 */
data class StockBalanceModel(
    val skuId: Long,
    val locationId: Long,
    val qty: Long,
    val lastSeq: Long,
    val modifiedAt: Instant?,
) {
    /**
     * 이동 1건을 잔고에 반영한다. qty=0 은 거부, 반영 결과가 음수면
     * [StockException.InsufficientStockException] — 음수재고/오버피킹은 여기서 차단된다.
     */
    fun apply(type: MovementType, qty: Long, refType: String? = null, refId: String? = null): StockApplication {
        if (qty == 0L) {
            throw StockException.InvalidQuantityException("이동 수량은 0 일 수 없습니다.")
        }
        val newQty = this.qty + qty
        if (newQty < 0) {
            throw StockException.InsufficientStockException(skuId, locationId, requested = -qty, available = this.qty)
        }
        val now = Instant.now()
        val movement = StockMovementModel(
            id = null,
            skuId = skuId,
            locationId = locationId,
            type = type,
            qty = qty,
            refType = refType,
            refId = refId,
            seq = lastSeq + 1,
            occurredAt = now,
        )
        return StockApplication(
            balance = copy(qty = newQty, lastSeq = lastSeq + 1, modifiedAt = now),
            movement = movement,
        )
    }

    companion object {
        /** 첫 이동 전의 빈 잔고(qty 0, seq 0) — 잔고 행이 아직 없을 때의 시작점. */
        fun empty(skuId: Long, locationId: Long): StockBalanceModel =
            StockBalanceModel(skuId = skuId, locationId = locationId, qty = 0, lastSeq = 0, modifiedAt = null)
    }
}

/** [StockBalanceModel.apply] 의 결과 — 갱신된 잔고와 그때 만들어진 원장 레코드 한 쌍. */
data class StockApplication(
    val balance: StockBalanceModel,
    val movement: StockMovementModel,
)
