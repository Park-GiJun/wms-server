package com.gijun.wms.master.infrastructure.adapter.out.persistence.partner

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import com.gijun.wms.master.domain.partner.PartnerModel
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
 * partner 테이블 매핑(V5__partner.sql). 도메인 모델과 1:1 변환만 담당 — 로직 없음.
 */
@Entity
@Table(name = "partner")
class PartnerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    val code: String,

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false, length = 20)
    val type: PartnerType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: PartnerStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "modified_at", nullable = false)
    val modifiedAt: Instant,
) {
    fun toModel(): PartnerModel = PartnerModel(
        id = id,
        code = code,
        name = name,
        type = type,
        status = status,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

    companion object {
        fun from(model: PartnerModel): PartnerJpaEntity = PartnerJpaEntity(
            id = model.id,
            code = model.code,
            name = model.name,
            type = model.type,
            status = model.status,
            createdAt = model.createdAt ?: Instant.now(),
            modifiedAt = model.modifiedAt ?: Instant.now(),
        )
    }
}
