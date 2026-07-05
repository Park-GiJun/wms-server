package com.gijun.wms.user.domain.enums

/**
 * 회원가입 수명주기: register 시 PENDING → 관리자 승인/거절로 ACTIVE / REJECTED.
 * 로그인(JWT 발급)은 ACTIVE 만 허용한다.
 */
enum class AppUserStatus {
    PENDING, ACTIVE, REJECTED
}
