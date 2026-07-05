package com.gijun.wms.user.application.dto.command

/** 회원가입 — PENDING 으로 생성된다. */
data class RegisterAppUserCommand(
    val email: String,
    val password: String,
)

/** 로그인 — ACTIVE 계정만 JWT 를 발급받는다. */
data class LoginAppUserCommand(
    val email: String,
    val password: String,
)

/** 관리자 승인. adminId 는 gateway 가 보장한 X-User-Id 에서 온다. */
data class ApproveAppUserCommand(
    val userId: Long,
    val adminId: Long,
)

/** 관리자 거절. adminId 는 gateway 가 보장한 X-User-Id 에서 온다. */
data class RejectAppUserCommand(
    val userId: Long,
    val adminId: Long,
)
