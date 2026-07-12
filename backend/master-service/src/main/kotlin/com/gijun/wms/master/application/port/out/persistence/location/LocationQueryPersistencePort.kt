package com.gijun.wms.master.application.port.out.persistence.location

import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.location.LocationModel

interface LocationQueryPersistencePort {
    fun existsByCode(code: String): Boolean
    fun findById(id: Long): LocationModel?
    fun findLocations(status: LocationStatus?, zone: String?): List<LocationModel>
}
