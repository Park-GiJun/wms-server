package com.gijun.wms.master.application.dto.result

import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.enums.LocationType
import com.gijun.wms.master.domain.location.LocationModel

data class LocationResult(
    val id: Long,
    val code: String,
    val zone: String,
    val type: LocationType,
    val status: LocationStatus,
) {
    companion object {
        fun from(model: LocationModel): LocationResult = LocationResult(
            id = requireNotNull(model.id) { "저장된 로케이션은 id 가 있어야 한다" },
            code = model.code,
            zone = model.zone,
            type = model.type,
            status = model.status,
        )
    }
}
