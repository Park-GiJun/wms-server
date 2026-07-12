package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.ListLocationsQuery
import com.gijun.wms.master.application.dto.result.LocationResult

interface ListLocationsQueryUseCase {
    fun listLocations(query: ListLocationsQuery): List<LocationResult>
}
