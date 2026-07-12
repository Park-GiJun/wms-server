package com.gijun.wms.master.domain.stock

import com.gijun.wms.master.domain.stock.exception.StockException
import com.gijun.wms.shared.event.MovementType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** 재고원장 도메인 규칙(부호 정책·불변식·TRANSFER 분해) 단위 테스트. */
class StockLedgerTest {

    private fun balance(qty: Long, lastSeq: Long = 0, skuId: Long = 1, locationId: Long = 10) =
        StockBalanceModel(skuId = skuId, locationId = locationId, qty = qty, lastSeq = lastSeq, modifiedAt = null)

    @Test
    fun `입고는 잔고와 seq 를 함께 올린다`() {
        val applied = StockLedger.receive(StockBalanceModel.empty(1, 10), qty = 7, refType = "INBOUND_ORDER", refId = "IB-1")
        assertEquals(7, applied.balance.qty)
        assertEquals(1, applied.balance.lastSeq)
        assertEquals(MovementType.RECEIPT, applied.movement.type)
        assertEquals(7, applied.movement.qty)
        assertEquals(1, applied.movement.seq)
    }

    @Test
    fun `입고 수량은 양수만 허용한다`() {
        assertFailsWith<StockException.InvalidQuantityException> {
            StockLedger.receive(balance(qty = 5), qty = -3)
        }
    }

    @Test
    fun `수량 0 이동은 거부한다`() {
        assertFailsWith<StockException.InvalidQuantityException> {
            StockLedger.adjust(balance(qty = 5), qtyDelta = 0)
        }
    }

    @Test
    fun `가용 재고를 넘는 차감은 차단한다`() {
        assertFailsWith<StockException.InsufficientStockException> {
            StockLedger.adjust(balance(qty = 5), qtyDelta = -6)
        }
    }

    @Test
    fun `조정은 음수 방향도 가용 잔고까지는 허용한다`() {
        val applied = StockLedger.adjust(balance(qty = 5, lastSeq = 3), qtyDelta = -5)
        assertEquals(0, applied.balance.qty)
        assertEquals(4, applied.balance.lastSeq)
        assertEquals(MovementType.ADJUSTMENT, applied.movement.type)
        assertEquals(-5, applied.movement.qty)
    }

    @Test
    fun `transfer 는 출발지 -qty 와 도착지 +qty 두 건으로 분해된다`() {
        val from = balance(qty = 10, lastSeq = 2, locationId = 10)
        val to = balance(qty = 1, lastSeq = 5, locationId = 20)

        val transferred = StockLedger.transfer(from, to, qty = 4, refType = "MOVE_ORDER", refId = "MV-1")

        assertEquals(6, transferred.outbound.balance.qty)
        assertEquals(-4, transferred.outbound.movement.qty)
        assertEquals(3, transferred.outbound.movement.seq)
        assertEquals(5, transferred.inbound.balance.qty)
        assertEquals(4, transferred.inbound.movement.qty)
        assertEquals(6, transferred.inbound.movement.seq)
        assertEquals(MovementType.TRANSFER, transferred.outbound.movement.type)
        assertEquals(MovementType.TRANSFER, transferred.inbound.movement.type)
        assertEquals("MV-1", transferred.outbound.movement.refId)
        assertEquals("MV-1", transferred.inbound.movement.refId)
    }

    @Test
    fun `transfer 는 출발지 가용 재고를 넘을 수 없다`() {
        assertFailsWith<StockException.InsufficientStockException> {
            StockLedger.transfer(balance(qty = 3, locationId = 10), balance(qty = 0, locationId = 20), qty = 5)
        }
    }

    @Test
    fun `같은 로케이션으로의 transfer 는 거부한다`() {
        assertFailsWith<StockException.SameLocationTransferException> {
            StockLedger.transfer(balance(qty = 5, locationId = 10), balance(qty = 0, locationId = 10), qty = 1)
        }
    }

    @Test
    fun `transfer 수량은 양수만 허용한다`() {
        assertFailsWith<StockException.InvalidQuantityException> {
            StockLedger.transfer(balance(qty = 5, locationId = 10), balance(qty = 0, locationId = 20), qty = 0)
        }
    }
}
