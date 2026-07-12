package com.gijun.wms.master.infrastructure.adapter.out.persistence.partner

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import org.springframework.data.jpa.repository.JpaRepository

interface PartnerJpaRepository : JpaRepository<PartnerJpaEntity, Long> {
    fun existsByCode(code: String): Boolean
    fun findByStatus(status: PartnerStatus): List<PartnerJpaEntity>
    fun findByType(type: PartnerType): List<PartnerJpaEntity>
    fun findByStatusAndType(status: PartnerStatus, type: PartnerType): List<PartnerJpaEntity>
}
