package com.gijun.wms.master.infrastructure.adapter.out.persistence.item

import org.springframework.data.jpa.repository.JpaRepository

interface SkuJpaRepository : JpaRepository<SkuJpaEntity, Long> {
    fun existsBySkuCode(skuCode: String): Boolean
    fun existsByBarcode(barcode: String): Boolean
    fun findBySkuCode(skuCode: String): SkuJpaEntity?
    fun findByBarcode(barcode: String): SkuJpaEntity?
    fun findByProductId(productId: Long): List<SkuJpaEntity>
}
