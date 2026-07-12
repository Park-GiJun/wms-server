package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.ListStockBalancesQuery
import com.gijun.wms.master.application.dto.result.StockBalanceResult

interface ListStockBalancesQueryUseCase {
    fun listBalances(query: ListStockBalancesQuery): List<StockBalanceResult>
}
