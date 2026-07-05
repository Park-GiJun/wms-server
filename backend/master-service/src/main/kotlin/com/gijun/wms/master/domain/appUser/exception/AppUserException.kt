package com.gijun.wms.master.domain.appUser.exception

import com.gijun.wms.shared.exception.ErrorCode
import com.gijun.wms.shared.exception.WmsException
import com.gijun.wms.master.domain.enums.AppUserStatus

/**
 * appUser 도메인 예외. 서브클래스가 [ErrorCode] 를 고정하고, 호출부는 컨텍스트(이메일 등)만 넘긴다.
 * message 를 생략하면 code.defaultMessage 가 쓰인다.
 */
sealed class AppUserException(
    code: ErrorCode,
    message: String = code.defaultMessage,
) : WmsException(code, message) {

    /** 이메일 중복 — 409 CONFLICT 로 매핑된다. */
    class DuplicateUserException(email: String) :
        AppUserException(ErrorCode.DUPLICATE_APP_USER_EMAIL, "이미 존재하는 이메일입니다: $email")

    /** 대상 사용자 없음 — 404. */
    class UserNotFoundException(userId: Long) :
        AppUserException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다: $userId")

    /** PENDING 이 아닌 사용자에 승인/거절 시도 — 409. */
    class NotPendingException(userId: Long?, status: AppUserStatus) :
        AppUserException(ErrorCode.CONFLICT, "승인 대기 상태가 아닙니다: id=$userId, status=$status")

    /** 비밀번호 정책 위반 — 400. */
    class WeakPasswordException(reason: String) :
        AppUserException(ErrorCode.INVALID_INPUT, "비밀번호 정책 위반: $reason")

    /** 로그인 실패 — 계정 존재 여부를 숨기기 위해 이메일/비밀번호를 구분하지 않는다. 401. */
    class InvalidCredentialsException :
        AppUserException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.")

    /** ACTIVE 가 아닌 계정의 로그인 시도 — 403. */
    class NotActiveException(status: AppUserStatus) :
        AppUserException(
            ErrorCode.FORBIDDEN,
            when (status) {
                AppUserStatus.PENDING -> "관리자 승인 대기 중인 계정입니다."
                AppUserStatus.REJECTED -> "가입이 거절된 계정입니다."
                AppUserStatus.ACTIVE -> "활성 계정입니다."   // 도달 불가 — when 완전성용
            },
        )
}
