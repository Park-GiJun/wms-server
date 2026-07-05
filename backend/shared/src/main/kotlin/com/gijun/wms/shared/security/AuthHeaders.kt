package com.gijun.wms.shared.security

/**
 * gateway 가 JWT 검증 후 백엔드로 전파하는 신원 헤더 이름.
 * 클라이언트가 직접 보낸 X-User-* 는 gateway 가 덮어쓰거나 제거하므로, 백엔드는 이 헤더를 신뢰한다.
 */
object AuthHeaders {
    const val USER_ID = "X-User-Id"
    const val USER_EMAIL = "X-User-Email"
    const val USER_ROLE = "X-User-Role"
}
