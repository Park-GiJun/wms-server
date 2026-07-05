package com.gijun.wms.user.domain.appUser

import com.gijun.wms.user.domain.appUser.exception.AppUserException
import com.gijun.wms.user.domain.enums.AppUserStatus
import com.gijun.wms.user.domain.enums.UserRoles
import java.time.Instant

/**
 * 신원 어그리게이트. 상태 전이 불변식(PENDING 에서만 승인/거절 가능)은 전부 이 안에서 지킨다.
 * register 시 PENDING → 관리자 approve/reject 로 ACTIVE/REJECTED. 로그인은 ACTIVE 만.
 */
data class AppUserModel(
    val id: Long?,
    val email: String,
    val passwordHash: String,
    val status: AppUserStatus,
    val userRole: UserRoles,
    val approvedBy: Long? = null,   // 승인/거절한 관리자 id (감사 이력)
    val approvedAt: Instant? = null,
    val createdAt: Instant?,
    val modifiedAt: Instant?,
) {
    fun approve(adminId: Long): AppUserModel {
        ensurePending()
        return copy(
            status = AppUserStatus.ACTIVE,
            approvedBy = adminId,
            approvedAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
    }

    fun reject(adminId: Long): AppUserModel {
        ensurePending()
        return copy(
            status = AppUserStatus.REJECTED,
            approvedBy = adminId,
            approvedAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
    }

    private fun ensurePending() {
        if (status != AppUserStatus.PENDING) {
            throw AppUserException.NotPendingException(id, status)
        }
    }

    companion object {
        /** 회원가입 — 항상 PENDING 으로 시작하고 관리자 승인을 기다린다. */
        fun register(email: String, passwordHash: String): AppUserModel = AppUserModel(
            id = null,
            email = email,
            passwordHash = passwordHash,
            status = AppUserStatus.PENDING,
            userRole = UserRoles.USER,
            createdAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
    }
}
