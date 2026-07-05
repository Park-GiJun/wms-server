package com.gijun.wms.master.infrastructure.adapter.`in`.appUser.web

import com.gijun.wms.master.application.dto.result.AppUserResult
import com.gijun.wms.master.application.dto.result.TokenResult
import com.gijun.wms.master.domain.enums.AppUserStatus
import com.gijun.wms.master.domain.enums.UserRoles
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterAppUserRequest(
    @field:NotBlank @field:Email
    val email: String,
    @field:NotBlank @field:Size(min = 8, max = 72)   // 72 = BCrypt 입력 상한
    val password: String,
)

data class LoginRequest(
    @field:NotBlank @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
)

data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
) {
    companion object {
        fun from(result: TokenResult): TokenResponse = TokenResponse(
            accessToken = result.accessToken,
            tokenType = result.tokenType,
            expiresInSeconds = result.expiresInSeconds,
        )
    }
}

data class AppUserResponse(
    val id: Long,
    val email: String,
    val status: AppUserStatus,
    val userRole: UserRoles,
) {
    companion object {
        fun from(result: AppUserResult): AppUserResponse = AppUserResponse(
            id = result.id,
            email = result.email,
            status = result.status,
            userRole = result.userRole,
        )
    }
}
