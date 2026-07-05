package com.gijun.wms.user.infrastructure.adapter.out.security

import com.gijun.wms.user.application.port.out.security.PasswordHasherPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

/** spring-security-crypto 의 BCrypt 구현 — 풀 security 스타터 없이 crypto 모듈만 쓴다. */
@Component
class BCryptPasswordHasherAdapter : PasswordHasherPort {

    private val encoder = BCryptPasswordEncoder()

    override fun hash(rawPassword: String): String =
        requireNotNull(encoder.encode(rawPassword)) { "BCrypt encode 가 null 을 반환" }

    override fun matches(rawPassword: String, passwordHash: String): Boolean =
        encoder.matches(rawPassword, passwordHash)
}
