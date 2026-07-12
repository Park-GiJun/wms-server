package com.gijun.wms.master.domain.location.exception

import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.shared.exception.ErrorCode
import com.gijun.wms.shared.exception.WmsException

/**
 * 로케이션 도메인 예외. 서브클래스가 [ErrorCode] 를 고정하고, 호출부는 컨텍스트(코드 등)만 넘긴다.
 */
sealed class LocationException(
    code: ErrorCode,
    message: String = code.defaultMessage,
) : WmsException(code, message) {

    /** 로케이션 코드 중복 — 409. */
    class DuplicateLocationCodeException(code: String) :
        LocationException(ErrorCode.CONFLICT, "이미 존재하는 로케이션 코드입니다: $code")

    /** 대상 로케이션 없음 — 404. */
    class LocationNotFoundException(locationId: Long) :
        LocationException(ErrorCode.NOT_FOUND, "로케이션을 찾을 수 없습니다: $locationId")

    /** 이미 목표 상태 — 409 (중복 요청 감지). */
    class AlreadyInStatusException(target: LocationStatus) :
        LocationException(ErrorCode.CONFLICT, "이미 $target 상태입니다.")
}
