package com.gijun.wms.master.domain.item

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.exception.ItemException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ItemModelTest {

    @Test
    fun `상품은 ACTIVE 로 생성된다`() {
        val product = ProductModel.create("P-001", "티셔츠", "의류", "상의", null)
        assertEquals(ItemStatus.ACTIVE, product.status)
    }

    @Test
    fun `상품 상태 전환 - ACTIVE 에서 INACTIVE 로`() {
        val product = ProductModel.create("P-001", "티셔츠", "의류", null, null)
        assertEquals(ItemStatus.INACTIVE, product.changeStatus(ItemStatus.INACTIVE).status)
    }

    @Test
    fun `상품을 같은 상태로 전환하면 예외`() {
        val product = ProductModel.create("P-001", "티셔츠", "의류", null, null)
        assertFailsWith<ItemException.AlreadyInStatusException> {
            product.changeStatus(ItemStatus.ACTIVE)
        }
    }

    @Test
    fun `SKU 는 ACTIVE 로 생성된다`() {
        val sku = SkuModel.create(1L, "P-001-BLK-L", null, listOf(OptionModel("색상", "블랙")), "EA")
        assertEquals(ItemStatus.ACTIVE, sku.status)
    }

    @Test
    fun `SKU 를 같은 상태로 전환하면 예외`() {
        val sku = SkuModel.create(1L, "P-001-BLK-L", null, emptyList(), "EA")
        assertFailsWith<ItemException.AlreadyInStatusException> {
            sku.changeStatus(ItemStatus.ACTIVE)
        }
    }

    @Test
    fun `SKU 수정은 barcode-options-unit 만 바꾼다`() {
        val sku = SkuModel.create(1L, "P-001-BLK-L", null, emptyList(), "EA")
        val updated = sku.update("8801234567890", listOf(OptionModel("색상", "블랙")), "BOX")
        assertEquals("P-001-BLK-L", updated.skuCode)
        assertEquals("8801234567890", updated.barcode)
        assertEquals("BOX", updated.unit)
        assertEquals(1, updated.options.size)
    }
}
