package com.gijun.wms.master.application.handler.query

import com.gijun.wms.master.application.dto.query.ListStockBalancesQuery
import com.gijun.wms.master.application.dto.query.ListStockMovementsQuery
import com.gijun.wms.master.application.dto.result.StockBalanceResult
import com.gijun.wms.master.application.dto.result.StockMovementHistoryResult
import com.gijun.wms.master.application.port.`in`.query.ListStockBalancesQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.ListStockMovementsQueryUseCase
import com.gijun.wms.master.application.port.out.persistence.stock.StockQueryPersistencePort
import com.gijun.wms.master.domain.stock.exception.StockException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockQueryHandler(
    private val stockQueryPersistencePort: StockQueryPersistencePort,
) : ListStockBalancesQueryUseCase, ListStockMovementsQueryUseCase {

    @Transactional(readOnly = true)
    override fun listBalances(query: ListStockBalancesQuery): List<StockBalanceResult> =
        stockQueryPersistencePort.findBalances(query.skuId, query.locationId)
            .map(StockBalanceResult::from)

    /** 이력은 무한히 자라는 append-only 라 필터 없는 전체 조회를 거부한다(400). */
    @Transactional(readOnly = true)
    override fun listMovements(query: ListStockMovementsQuery): List<StockMovementHistoryResult> {
        if (query.skuId == null && query.locationId == null) {
            throw StockException.EmptyMovementFilterException()
        }
        return stockQueryPersistencePort.findMovements(query.skuId, query.locationId)
            .map(StockMovementHistoryResult::from)
    }
}
