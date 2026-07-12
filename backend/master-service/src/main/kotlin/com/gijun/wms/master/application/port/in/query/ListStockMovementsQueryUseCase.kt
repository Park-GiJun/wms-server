package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.ListStockMovementsQuery
import com.gijun.wms.master.application.dto.result.StockMovementHistoryResult

interface ListStockMovementsQueryUseCase {
    fun listMovements(query: ListStockMovementsQuery): List<StockMovementHistoryResult>
}
