package com.gijun.wms.shared.security

/**
 * JWT 에서 추출한 검증된 신원. gateway 가 이 값을 X-User-* 헤더로 백엔드에 전파한다.
 */
data class JwtClaims(
    val userId: String,
    val email: String,
    val role: String,
)
