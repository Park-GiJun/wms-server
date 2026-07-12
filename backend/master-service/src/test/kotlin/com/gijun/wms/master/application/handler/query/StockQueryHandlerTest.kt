package com.gijun.wms.master.application.handler.query

import com.gijun.wms.master.application.dto.query.ListStockBalancesQuery
import com.gijun.wms.master.application.dto.query.ListStockMovementsQuery
import com.gijun.wms.master.application.port.out.persistence.stock.StockQueryPersistencePort
import com.gijun.wms.master.domain.stock.StockBalanceModel
import com.gijun.wms.master.domain.stock.StockMovementModel
import com.gijun.wms.master.domain.stock.exception.StockException
import com.gijun.wms.shared.event.MovementType
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** 쿼리 핸들러 규칙(무필터 이력 조회 거부, 결과 매핑) 단위 테스트. */
class StockQueryHandlerTest {

    private val movement = StockMovementModel(
        id = 1, skuId = 1, locationId = 10, type = MovementType.RECEIPT,
        qty = 5, refType = null, refId = null, seq = 1, occurredAt = Instant.EPOCH,
    )
    private val handler = StockQueryHandler(
        FakeStockQueryPort(
            balances = listOf(StockBalanceModel(skuId = 1, locationId = 10, qty = 5, lastSeq = 1, modifiedAt = null)),
            movements = listOf(movement),
        ),
    )

    @Test
    fun `잔고 목록은 필터 없이도 조회된다`() {
        val results = handler.listBalances(ListStockBalancesQuery(skuId = null, locationId = null))
        assertEquals(1, results.size)
        assertEquals(5, results.first().qty)
    }

    @Test
    fun `원장 이력은 필터 없는 전체 조회를 거부한다`() {
        assertFailsWith<StockException.EmptyMovementFilterException> {
            handler.listMovements(ListStockMovementsQuery(skuId = null, locationId = null))
        }
    }

    @Test
    fun `원장 이력은 필터가 하나라도 있으면 조회된다`() {
        val results = handler.listMovements(ListStockMovementsQuery(skuId = 1, locationId = null))
        assertEquals(1, results.size)
        assertEquals(MovementType.RECEIPT, results.first().type)
        assertEquals(1, results.first().seq)
    }

    private class FakeStockQueryPort(
        private val balances: List<StockBalanceModel>,
        private val movements: List<StockMovementModel>,
    ) : StockQueryPersistencePort {
        override fun findBalances(skuId: Long?, locationId: Long?): List<StockBalanceModel> = balances
        override fun findMovements(skuId: Long?, locationId: Long?): List<StockMovementModel> = movements
    }
}
