package com.gijun.wms.master.infrastructure.adapter.`in`.location.web

import com.gijun.wms.master.application.dto.result.LocationResult
import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.enums.LocationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateLocationRequest(
    @field:NotBlank @field:Size(max = 50)
    val code: String,
    @field:NotBlank @field:Size(max = 50)
    val zone: String,
    val type: LocationType,
)

data class UpdateLocationRequest(
    @field:NotBlank @field:Size(max = 50)
    val zone: String,
    val type: LocationType,
)

/** 활성/비활성 전환 요청. */
data class ChangeLocationStatusRequest(
    val status: LocationStatus,
)

data class LocationResponse(
    val id: Long,
    val code: String,
    val zone: String,
    val type: LocationType,
    val status: LocationStatus,
) {
    companion object {
        fun from(result: LocationResult): LocationResponse = LocationResponse(
            id = result.id,
            code = result.code,
            zone = result.zone,
            type = result.type,
            status = result.status,
        )
    }
}
