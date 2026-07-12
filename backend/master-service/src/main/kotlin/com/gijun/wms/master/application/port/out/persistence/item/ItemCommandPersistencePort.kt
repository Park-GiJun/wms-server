package com.gijun.wms.master.application.port.out.persistence.item

import com.gijun.wms.master.domain.item.ProductModel
import com.gijun.wms.master.domain.item.SkuModel

interface ItemCommandPersistencePort {
    fun saveProduct(model: ProductModel): ProductModel
    fun findProductById(id: Long): ProductModel?
    fun saveSku(model: SkuModel): SkuModel
    fun findSkuById(id: Long): SkuModel?
}
