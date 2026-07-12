package com.gijun.wms.master.application.dto.query

import com.gijun.wms.master.domain.enums.LocationStatus

/** 로케이션 단건 조회. */
data class GetLocationQuery(
    val locationId: Long,
)

/** 로케이션 목록 조회 — status/zone 필터는 각각 null 이면 전체. */
data class ListLocationsQuery(
    val status: LocationStatus?,
    val zone: String?,
)
