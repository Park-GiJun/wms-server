package com.gijun.wms.master.infrastructure.adapter.`in`.stock.web

import com.gijun.wms.master.application.dto.query.ListStockBalancesQuery
import com.gijun.wms.master.application.port.`in`.query.ListStockBalancesQueryUseCase
import com.gijun.wms.shared.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** 재고 조회 API — 원장의 파생 상태(stock_balance)를 읽는다. 쓰기는 /api/stock-movements 만. */
@RestController
@RequestMapping("/api/inventory")
class InventoryController(
    private val listStockBalancesQueryUseCase: ListStockBalancesQueryUseCase,
) {

    /** 현재 잔고 목록 — skuId/locationId 필터는 각각 null 이면 전체. */
    @GetMapping("/balances")
    fun balances(
        @RequestParam(required = false) skuId: Long?,
        @RequestParam(required = false) locationId: Long?,
    ): ApiResponse<List<StockBalanceResponse>> {
        val results = listStockBalancesQueryUseCase.listBalances(ListStockBalancesQuery(skuId, locationId))
        return ApiResponse.ok(results.map(StockBalanceResponse::from))
    }
}
