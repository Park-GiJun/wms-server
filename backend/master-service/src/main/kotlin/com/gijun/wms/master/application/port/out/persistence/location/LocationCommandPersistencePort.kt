package com.gijun.wms.master.application.port.out.persistence.location

import com.gijun.wms.master.domain.location.LocationModel

interface LocationCommandPersistencePort {
    fun save(model: LocationModel): LocationModel
    fun findById(id: Long): LocationModel?
}
