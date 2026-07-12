package com.gijun.wms.master.infrastructure.adapter.`in`.stock.web

import com.gijun.wms.master.application.dto.command.RecordAdjustmentCommand
import com.gijun.wms.master.application.dto.command.RecordReceiptCommand
import com.gijun.wms.master.application.dto.command.RecordTransferCommand
import com.gijun.wms.master.application.port.`in`.command.RecordAdjustmentCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.RecordReceiptCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.RecordTransferCommandUseCase
import com.gijun.wms.shared.web.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 재고원장 커맨드 API — 모든 물리 이동이 이 진입점으로 환원된다.
 * 재고 부족은 422, 미존재 SKU/로케이션은 404, 비활성 대상 유입은 409.
 */
@RestController
@RequestMapping("/api/stock-movements")
class StockMovementController(
    private val recordReceiptCommandUseCase: RecordReceiptCommandUseCase,
    private val recordTransferCommandUseCase: RecordTransferCommandUseCase,
    private val recordAdjustmentCommandUseCase: RecordAdjustmentCommandUseCase,
) {

    /** 입고 — qty 양수만. */
    @PostMapping("/receive")
    @ResponseStatus(HttpStatus.CREATED)
    fun receive(@Valid @RequestBody request: ReceiveStockRequest): ApiResponse<StockMovementResponse> {
        val result = recordReceiptCommandUseCase.receive(
            RecordReceiptCommand(
                skuId = request.skuId,
                locationId = request.locationId,
                qty = request.qty,
                refType = request.refType,
                refId = request.refId,
            ),
        )
        return ApiResponse.ok(StockMovementResponse.from(result))
    }

    /** 로케이션 이동 — movement 2건(출발지 -qty / 도착지 +qty)으로 기록된다. */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    fun transfer(@Valid @RequestBody request: TransferStockRequest): ApiResponse<TransferResponse> {
        val result = recordTransferCommandUseCase.transfer(
            RecordTransferCommand(
                skuId = request.skuId,
                fromLocationId = request.fromLocationId,
                toLocationId = request.toLocationId,
                qty = request.qty,
                refType = request.refType,
                refId = request.refId,
            ),
        )
        return ApiResponse.ok(TransferResponse.from(result))
    }

    /** 재고 조정(실사 차이 등) — qtyDelta 양방향(±), 음수 잔고는 차단(422). */
    @PostMapping("/adjust")
    @ResponseStatus(HttpStatus.CREATED)
    fun adjust(@Valid @RequestBody request: AdjustStockRequest): ApiResponse<StockMovementResponse> {
        val result = recordAdjustmentCommandUseCase.adjust(
            RecordAdjustmentCommand(
                skuId = request.skuId,
                locationId = request.locationId,
                qtyDelta = request.qtyDelta,
                refType = request.refType,
                refId = request.refId,
            ),
        )
        return ApiResponse.ok(StockMovementResponse.from(result))
    }
}
