package com.gijun.wms.user.infrastructure.adapter.out.persistence.appUser

import com.gijun.wms.user.domain.appUser.AppUserModel
import com.gijun.wms.user.domain.enums.AppUserStatus
import com.gijun.wms.user.domain.enums.UserRoles
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
 * app_user 테이블 매핑(V2__app_user.sql). 도메인 모델과 1:1 변환만 담당 — 로직 없음.
 */
@Entity
@Table(name = "app_user")
class AppUserJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: AppUserStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    val userRole: UserRoles,

    @Column(name = "approved_by")
    val approvedBy: Long? = null,

    @Column(name = "approved_at")
    val approvedAt: Instant? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "modified_at", nullable = false)
    val modifiedAt: Instant,
) {
    fun toModel(): AppUserModel = AppUserModel(
        id = id,
        email = email,
        passwordHash = passwordHash,
        status = status,
        userRole = userRole,
        approvedBy = approvedBy,
        approvedAt = approvedAt,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

    companion object {
        fun from(model: AppUserModel): AppUserJpaEntity = AppUserJpaEntity(
            id = model.id,
            email = model.email,
            passwordHash = model.passwordHash,
            status = model.status,
            userRole = model.userRole,
            approvedBy = model.approvedBy,
            approvedAt = model.approvedAt,
            createdAt = model.createdAt ?: Instant.now(),
            modifiedAt = model.modifiedAt ?: Instant.now(),
        )
    }
}
