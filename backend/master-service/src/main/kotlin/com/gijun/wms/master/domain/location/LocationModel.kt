package com.gijun.wms.master.domain.location

import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.enums.LocationType
import com.gijun.wms.master.domain.location.exception.LocationException
import java.time.Instant

/**
 * 로케이션 — 재고원장 (sku, location) 축의 location 쪽 마스터.
 * 계층(창고>존>셀)은 지금은 [zone] 문자열로 표현하고, 필요해지면 트리로 승격한다.
 */
data class LocationModel(
    val id: Long?,
    val code: String,
    val zone: String,
    val type: LocationType,
    val status: LocationStatus,
    val createdAt: Instant?,
    val modifiedAt: Instant?,
) {
    /** code 는 비즈니스 키라 변경 불가 — 용도 변경(재배치)만 허용. */
    fun update(zone: String, type: LocationType): LocationModel = copy(
        zone = zone,
        type = type,
        modifiedAt = Instant.now(),
    )

    /** ACTIVE ↔ INACTIVE 전환. 같은 상태로의 전환은 거부한다(중복 요청 감지). */
    fun changeStatus(target: LocationStatus): LocationModel {
        if (status == target) {
            throw LocationException.AlreadyInStatusException(target)
        }
        return copy(status = target, modifiedAt = Instant.now())
    }

    companion object {
        fun create(code: String, zone: String, type: LocationType): LocationModel = LocationModel(
            id = null,
            code = code,
            zone = zone,
            type = type,
            status = LocationStatus.ACTIVE,
            createdAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
    }
}
