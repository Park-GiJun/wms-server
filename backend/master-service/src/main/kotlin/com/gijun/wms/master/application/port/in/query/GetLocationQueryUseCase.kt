package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.GetLocationQuery
import com.gijun.wms.master.application.dto.result.LocationResult

interface GetLocationQueryUseCase {
    fun getLocation(query: GetLocationQuery): LocationResult
}
