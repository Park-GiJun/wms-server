package com.gijun.wms.master.infrastructure.adapter.out.persistence.item

import com.gijun.wms.master.domain.enums.ItemStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<ProductJpaEntity, Long> {
    fun existsByCode(code: String): Boolean
    fun findByStatus(status: ItemStatus): List<ProductJpaEntity>
}
