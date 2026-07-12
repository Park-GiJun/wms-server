package com.gijun.wms.master.application.port.out.persistence.item

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.ProductModel
import com.gijun.wms.master.domain.item.SkuModel

interface ItemQueryPersistencePort {
    fun existsProductByCode(code: String): Boolean
    fun findProductById(id: Long): ProductModel?
    fun findProducts(status: ItemStatus?): List<ProductModel>
    fun findSkusByProductId(productId: Long): List<SkuModel>
    fun existsSkuByCode(skuCode: String): Boolean
    fun existsSkuByBarcode(barcode: String): Boolean
    fun findSkuByCode(skuCode: String): SkuModel?
    fun findSkuByBarcode(barcode: String): SkuModel?
}
