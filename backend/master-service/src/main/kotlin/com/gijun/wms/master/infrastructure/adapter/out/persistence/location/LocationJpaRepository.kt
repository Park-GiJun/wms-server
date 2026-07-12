package com.gijun.wms.master.infrastructure.adapter.out.persistence.location

import com.gijun.wms.master.domain.enums.LocationStatus
import org.springframework.data.jpa.repository.JpaRepository

interface LocationJpaRepository : JpaRepository<LocationJpaEntity, Long> {
    fun existsByCode(code: String): Boolean
    fun findByStatus(status: LocationStatus): List<LocationJpaEntity>
    fun findByZone(zone: String): List<LocationJpaEntity>
    fun findByStatusAndZone(status: LocationStatus, zone: String): List<LocationJpaEntity>
}
