package com.gijun.wms.user.infrastructure.adapter.`in`.appUser.web

import com.gijun.wms.shared.exception.ErrorCode
import com.gijun.wms.shared.exception.WmsException
import com.gijun.wms.shared.security.AuthHeaders
import com.gijun.wms.shared.web.ApiResponse
import com.gijun.wms.user.application.dto.command.ApproveAppUserCommand
import com.gijun.wms.user.application.dto.command.RejectAppUserCommand
import com.gijun.wms.user.application.port.`in`.command.ApproveAppUserCommandUseCase
import com.gijun.wms.user.application.port.`in`.command.RejectAppUserCommandUseCase
import com.gijun.wms.user.domain.enums.UserRoles
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 가입 승인/거절 — ADMIN 전용. 신원은 gateway 가 보장한 X-User-* 헤더에서 읽는다(직접 인증 없음).
 */
@RestController
@RequestMapping("/api/users")
class AppUserAdminController(
    private val approveAppUserCommandUseCase: ApproveAppUserCommandUseCase,
    private val rejectAppUserCommandUseCase: RejectAppUserCommandUseCase,
) {

    @PostMapping("/{userId}/approve")
    fun approve(
        @PathVariable userId: Long,
        @RequestHeader(AuthHeaders.USER_ID) adminId: Long,
        @RequestHeader(AuthHeaders.USER_ROLE) role: String,
    ): ApiResponse<AppUserResponse> {
        requireAdmin(role)
        val result = approveAppUserCommandUseCase.approve(ApproveAppUserCommand(userId, adminId))
        return ApiResponse.ok(AppUserResponse.from(result))
    }

    @PostMapping("/{userId}/reject")
    fun reject(
        @PathVariable userId: Long,
        @RequestHeader(AuthHeaders.USER_ID) adminId: Long,
        @RequestHeader(AuthHeaders.USER_ROLE) role: String,
    ): ApiResponse<AppUserResponse> {
        requireAdmin(role)
        val result = rejectAppUserCommandUseCase.reject(RejectAppUserCommand(userId, adminId))
        return ApiResponse.ok(AppUserResponse.from(result))
    }

    private fun requireAdmin(role: String) {
        if (role != UserRoles.ADMIN.name) {
            throw WmsException(ErrorCode.FORBIDDEN)
        }
    }
}
