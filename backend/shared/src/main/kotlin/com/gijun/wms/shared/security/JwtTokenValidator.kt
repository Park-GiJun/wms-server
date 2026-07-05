package com.gijun.wms.shared.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey

/**
 * JWT 서명·issuer 검증기. **발급은 user-service, 검증은 gateway** 가 하지만 둘은 동일한
 * secret/issuer 를 공유하므로 검증 로직을 shared 에 둔다(발급 책임은 user 의 토큰 어댑터).
 */
class JwtTokenValidator(
    secret: String,
    private val issuer: String,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))

    /** 유효하면 [JwtClaims], 서명/만료/issuer 불일치 등 무효면 null. */
    fun validate(token: String): JwtClaims? =
        try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .payload
            JwtClaims(
                userId = claims.subject,
                email = claims["email", String::class.java] ?: "",
                role = claims["role", String::class.java] ?: "USER",
            )
        } catch (_: JwtException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
}
