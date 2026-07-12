package com.gijun.wms.master.application.handler.command

import com.gijun.wms.master.application.dto.command.RecordAdjustmentCommand
import com.gijun.wms.master.application.dto.command.RecordReceiptCommand
import com.gijun.wms.master.application.dto.command.RecordTransferCommand
import com.gijun.wms.master.application.port.out.persistence.item.ItemQueryPersistencePort
import com.gijun.wms.master.application.port.out.persistence.location.LocationQueryPersistencePort
import com.gijun.wms.master.application.port.out.persistence.stock.StockCommandPersistencePort
import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.enums.LocationType
import com.gijun.wms.master.domain.item.ProductModel
import com.gijun.wms.master.domain.item.SkuModel
import com.gijun.wms.master.domain.item.exception.ItemException
import com.gijun.wms.master.domain.location.LocationModel
import com.gijun.wms.master.domain.stock.StockApplication
import com.gijun.wms.master.domain.stock.StockBalanceModel
import com.gijun.wms.master.domain.stock.StockMovementModel
import com.gijun.wms.master.domain.stock.exception.StockException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** 핸들러 배관(마스터 검증 정책·락 획득 순서·결과 조립) 단위 테스트 — 원장 규칙 자체는 [StockLedgerTest]. */
class StockCommandHandlerTest {

    private val activeSku = sku(id = 1, status = ItemStatus.ACTIVE)
    private val inactiveSku = sku(id = 2, status = ItemStatus.INACTIVE)
    private val stockPort = FakeStockPort()
    private val handler = StockCommandHandler(
        stockCommandPersistencePort = stockPort,
        itemQueryPersistencePort = FakeItemPort(mapOf(1L to activeSku, 2L to inactiveSku)),
        locationQueryPersistencePort = FakeLocationPort(
            mapOf(
                10L to location(id = 10, status = LocationStatus.ACTIVE),
                20L to location(id = 20, status = LocationStatus.ACTIVE),
                30L to location(id = 30, status = LocationStatus.INACTIVE),
            ),
        ),
    )

    @Test
    fun `입고는 잔고를 만들고 movement 와 적용 후 잔고를 돌려준다`() {
        val result = handler.receive(RecordReceiptCommand(skuId = 1, locationId = 10, qty = 7, refType = null, refId = null))
        assertEquals(7, result.balanceQty)
        assertEquals(1, result.seq)
        assertEquals(7, stockPort.balances.getValue(1L to 10L).qty)
    }

    @Test
    fun `미존재 SKU 입고는 404 예외`() {
        assertFailsWith<ItemException.SkuNotFoundException> {
            handler.receive(RecordReceiptCommand(skuId = 99, locationId = 10, qty = 1, refType = null, refId = null))
        }
    }

    @Test
    fun `비활성 로케이션으로의 입고는 거부한다`() {
        assertFailsWith<StockException.InactiveTargetException> {
            handler.receive(RecordReceiptCommand(skuId = 1, locationId = 30, qty = 1, refType = null, refId = null))
        }
    }

    @Test
    fun `비활성 SKU 도 조정은 허용한다 - 실사는 현실의 반영`() {
        val result = handler.adjust(RecordAdjustmentCommand(skuId = 2, locationId = 10, qtyDelta = 3, refType = null, refId = null))
        assertEquals(3, result.balanceQty)
    }

    @Test
    fun `transfer 는 잔고 행을 locationId 오름차순으로 잠근다`() {
        handler.receive(RecordReceiptCommand(skuId = 1, locationId = 20, qty = 5, refType = null, refId = null))
        stockPort.lockOrder.clear()

        // from(20) > to(10) 인 요청 — 락은 [10, 20] 순서여야 한다(데드락 방지).
        handler.transfer(RecordTransferCommand(skuId = 1, fromLocationId = 20, toLocationId = 10, qty = 2, refType = null, refId = null))
        assertEquals(listOf(10L, 20L), stockPort.lockOrder)
    }

    @Test
    fun `transfer 결과는 락 순서와 무관하게 원래 방향을 유지한다`() {
        handler.receive(RecordReceiptCommand(skuId = 1, locationId = 20, qty = 5, refType = null, refId = null))

        val result = handler.transfer(RecordTransferCommand(skuId = 1, fromLocationId = 20, toLocationId = 10, qty = 2, refType = null, refId = null))
        assertEquals(20, result.outbound.locationId)
        assertEquals(-2, result.outbound.qty)
        assertEquals(3, result.outbound.balanceQty)
        assertEquals(10, result.inbound.locationId)
        assertEquals(2, result.inbound.qty)
        assertEquals(2, result.inbound.balanceQty)
    }

    @Test
    fun `비활성 로케이션에서의 반출 transfer 는 허용한다`() {
        // 30(INACTIVE)에 실사 조정으로 재고를 만든 뒤 ACTIVE 로케이션으로 빼내는 시나리오.
        handler.adjust(RecordAdjustmentCommand(skuId = 1, locationId = 30, qtyDelta = 4, refType = null, refId = null))
        val result = handler.transfer(RecordTransferCommand(skuId = 1, fromLocationId = 30, toLocationId = 10, qty = 4, refType = null, refId = null))
        assertEquals(0, result.outbound.balanceQty)
        assertEquals(4, result.inbound.balanceQty)
    }

    // ── fixtures & fakes ─────────────────────────────────────────────────────

    private fun sku(id: Long, status: ItemStatus) = SkuModel(
        id = id, productId = 1, skuCode = "SKU-$id", barcode = null,
        options = emptyList(), unit = "EA", status = status, createdAt = null, modifiedAt = null,
    )

    private fun location(id: Long, status: LocationStatus) = LocationModel(
        id = id, code = "LOC-$id", zone = "A", type = LocationType.STORAGE,
        status = status, createdAt = null, modifiedAt = null,
    )

    /** 인메모리 원장 포트 — ensureAndLock 호출 순서(lockOrder)를 기록해 데드락 방지 순서를 검증한다. */
    private class FakeStockPort : StockCommandPersistencePort {
        val balances = mutableMapOf<Pair<Long, Long>, StockBalanceModel>()
        val lockOrder = mutableListOf<Long>()
        private var nextId = 1L

        override fun ensureAndLock(skuId: Long, locationId: Long): StockBalanceModel {
            lockOrder += locationId
            return balances.getOrPut(skuId to locationId) { StockBalanceModel.empty(skuId, locationId) }
        }

        override fun persist(application: StockApplication): StockMovementModel {
            balances[application.balance.skuId to application.balance.locationId] = application.balance
            return application.movement.copy(id = nextId++)
        }
    }

    private class FakeItemPort(private val skus: Map<Long, SkuModel>) : ItemQueryPersistencePort {
        override fun findSkuById(id: Long): SkuModel? = skus[id]
        override fun existsProductByCode(code: String): Boolean = throw UnsupportedOperationException()
        override fun findProductById(id: Long): ProductModel? = throw UnsupportedOperationException()
        override fun findProducts(status: ItemStatus?): List<ProductModel> = throw UnsupportedOperationException()
        override fun findSkusByProductId(productId: Long): List<SkuModel> = throw UnsupportedOperationException()
        override fun existsSkuByCode(skuCode: String): Boolean = throw UnsupportedOperationException()
        override fun existsSkuByBarcode(barcode: String): Boolean = throw UnsupportedOperationException()
        override fun findSkuByCode(skuCode: String): SkuModel? = throw UnsupportedOperationException()
        override fun findSkuByBarcode(barcode: String): SkuModel? = throw UnsupportedOperationException()
    }

    private class FakeLocationPort(private val locations: Map<Long, LocationModel>) : LocationQueryPersistencePort {
        override fun findById(id: Long): LocationModel? = locations[id]
        override fun existsByCode(code: String): Boolean = throw UnsupportedOperationException()
        override fun findLocations(status: LocationStatus?, zone: String?): List<LocationModel> =
            throw UnsupportedOperationException()
    }
}
