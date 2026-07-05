package com.gijun.wms.user.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * JWT 발급 설정. secret/issuer 는 공통 application.yml(gateway 검증기와 동일 값 공유),
 * 유효시간은 config-repo/user-service.yml 의 jwt.access-token-validity-minutes.
 */
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val issuer: String,
    val accessTokenValidityMinutes: Long = 1440,
)
