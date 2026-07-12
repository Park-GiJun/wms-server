package com.gijun.wms.master.infrastructure.adapter.out.persistence.location

import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.enums.LocationType
import com.gijun.wms.master.domain.location.LocationModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * location 테이블 매핑(V4__location.sql). 도메인 모델과 1:1 변환만 담당 — 로직 없음.
 */
@Entity
@Table(name = "location")
class LocationJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    val code: String,

    @Column(nullable = false, length = 50)
    val zone: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false, length = 20)
    val type: LocationType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: LocationStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "modified_at", nullable = false)
    val modifiedAt: Instant,
) {
    fun toModel(): LocationModel = LocationModel(
        id = id,
        code = code,
        zone = zone,
        type = type,
        status = status,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

    companion object {
        fun from(model: LocationModel): LocationJpaEntity = LocationJpaEntity(
            id = model.id,
            code = model.code,
            zone = model.zone,
            type = model.type,
            status = model.status,
            createdAt = model.createdAt ?: Instant.now(),
            modifiedAt = model.modifiedAt ?: Instant.now(),
        )
    }
}
