package com.gijun.wms.master.application.dto.command

import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.enums.LocationType

/** 로케이션 생성 — ACTIVE 로 시작한다. */
data class CreateLocationCommand(
    val code: String,
    val zone: String,
    val type: LocationType,
)

/** 로케이션 수정 — code 는 비즈니스 키라 변경 불가. */
data class UpdateLocationCommand(
    val locationId: Long,
    val zone: String,
    val type: LocationType,
)

/** 로케이션 활성/비활성 전환 — 마스터는 삭제 대신 INACTIVE. */
data class ChangeLocationStatusCommand(
    val locationId: Long,
    val status: LocationStatus,
)
