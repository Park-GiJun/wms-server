package com.gijun.wms.user.application.port.out.token

import com.gijun.wms.user.application.dto.result.TokenResult
import com.gijun.wms.user.domain.enums.UserRoles

/**
 * JWT 발급 추상화 — 구현은 infrastructure(jjwt). 클레임 계약은 shared 의 JwtTokenValidator 와
 * 동일해야 한다: subject=userId, email, role + issuer.
 */
interface TokenIssuerPort {
    fun issue(userId: Long, email: String, role: UserRoles): TokenResult
}
