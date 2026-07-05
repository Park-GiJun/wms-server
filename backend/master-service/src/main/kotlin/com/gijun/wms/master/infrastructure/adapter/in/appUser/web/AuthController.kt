package com.gijun.wms.master.infrastructure.adapter.`in`.appUser.web

import com.gijun.wms.shared.web.ApiResponse
import com.gijun.wms.master.application.dto.command.LoginAppUserCommand
import com.gijun.wms.master.application.dto.command.RegisterAppUserCommand
import com.gijun.wms.master.application.dto.query.VerifyAppUserQuery
import com.gijun.wms.master.application.port.`in`.command.LoginAppUserCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.RegisterAppUserCommandUseCase
import com.gijun.wms.master.application.port.`in`.query.VerifyAppUserQueryUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 공개 인증 API — gateway 공개 경로(/api/auth 하위)라 X-User-* 헤더 없이 들어온다.
 * 가입은 PENDING 으로 저장되고 관리자 승인 후 로그인 가능하다.
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerAppUserCommandUseCase: RegisterAppUserCommandUseCase,
    private val loginAppUserCommandUseCase: LoginAppUserCommandUseCase,
    private val verifyAppUserQueryUseCase: VerifyAppUserQueryUseCase,
) {

    /** 로그인 — ACTIVE 계정만 JWT 발급. 자격 불일치 401, 승인 대기/거절 403. */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ApiResponse<TokenResponse> {
        val result = loginAppUserCommandUseCase.login(
            LoginAppUserCommand(email = request.email, password = request.password),
        )
        return ApiResponse.ok(TokenResponse.from(result))
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterAppUserRequest): ApiResponse<AppUserResponse> {
        val result = registerAppUserCommandUseCase.register(
            RegisterAppUserCommand(email = request.email, password = request.password),
        )
        return ApiResponse.ok(AppUserResponse.from(result))
    }

    /** 가입 전 이메일 중복 확인 — 사용 가능하면 200, 중복이면 409. */
    @GetMapping("/check-email")
    fun checkEmail(@RequestParam email: String): ApiResponse<Unit> {
        verifyAppUserQueryUseCase.verifyAppUser(VerifyAppUserQuery(email))
        return ApiResponse.ok(Unit)
    }
}
