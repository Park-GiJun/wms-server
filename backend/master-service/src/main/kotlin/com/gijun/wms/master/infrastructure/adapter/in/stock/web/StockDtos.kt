package com.gijun.wms.master.infrastructure.adapter.`in`.stock.web

import com.gijun.wms.master.application.dto.result.StockMovementResult
import com.gijun.wms.master.application.dto.result.TransferResult
import com.gijun.wms.shared.event.MovementType
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.Instant

data class ReceiveStockRequest(
    val skuId: Long,
    val locationId: Long,
    @field:Positive
    val qty: Long,
    @field:Size(max = 30)
    val refType: String? = null,
    @field:Size(max = 100)
    val refId: String? = null,
)

data class TransferStockRequest(
    val skuId: Long,
    val fromLocationId: Long,
    val toLocationId: Long,
    @field:Positive
    val qty: Long,
    @field:Size(max = 30)
    val refType: String? = null,
    @field:Size(max = 100)
    val refId: String? = null,
)

/** qtyDelta 는 양방향(±) — 0 거부와 음수 잔고 차단은 도메인 몫이라 어노테이션 없음. */
data class AdjustStockRequest(
    val skuId: Long,
    val locationId: Long,
    val qtyDelta: Long,
    @field:Size(max = 30)
    val refType: String? = null,
    @field:Size(max = 100)
    val refId: String? = null,
)

data class StockMovementResponse(
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
        fun from(result: StockMovementResult): StockMovementResponse = StockMovementResponse(
            id = result.id,
            skuId = result.skuId,
            locationId = result.locationId,
            type = result.type,
            qty = result.qty,
            refType = result.refType,
            refId = result.refId,
            seq = result.seq,
            occurredAt = result.occurredAt,
            balanceQty = result.balanceQty,
        )
    }
}

/** transfer 응답 — 출발지(-qty)/도착지(+qty) 두 건. */
data class TransferResponse(
    val outbound: StockMovementResponse,
    val inbound: StockMovementResponse,
) {
    companion object {
        fun from(result: TransferResult): TransferResponse = TransferResponse(
            outbound = StockMovementResponse.from(result.outbound),
            inbound = StockMovementResponse.from(result.inbound),
        )
    }
}
