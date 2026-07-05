package com.gijun.wms.user.application.port.out.security

/** 비밀번호 해싱 추상화 — 구현은 infrastructure(spring-security-crypto BCrypt)에 둔다. */
interface PasswordHasherPort {
    fun hash(rawPassword: String): String
    fun matches(rawPassword: String, passwordHash: String): Boolean
}
