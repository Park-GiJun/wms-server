package com.gijun.wms.master.infrastructure.adapter.out.token

import com.gijun.wms.master.application.dto.result.TokenResult
import com.gijun.wms.master.application.port.out.token.TokenIssuerPort
import com.gijun.wms.master.domain.enums.UserRoles
import com.gijun.wms.master.infrastructure.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Duration
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.stereotype.Component

/**
 * jjwt 발급 어댑터. 클레임 계약(subject=userId, email, role, issuer)은 shared 의
 * JwtTokenValidator(gateway 검증)와 반드시 일치해야 한다.
 */
@Component
class JjwtTokenIssuerAdapter(
    private val jwtProperties: JwtProperties,
) : TokenIssuerPort {

    private val key: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(Charsets.UTF_8))

    override fun issue(userId: Long, email: String, role: UserRoles): TokenResult {
        val now = Instant.now()
        val validity = Duration.ofMinutes(jwtProperties.accessTokenValidityMinutes)
        val accessToken = Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role.name)
            .issuer(jwtProperties.issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(validity)))
            .signWith(key)
            .compact()
        return TokenResult(
            accessToken = accessToken,
            expiresInSeconds = validity.seconds,
        )
    }
}
