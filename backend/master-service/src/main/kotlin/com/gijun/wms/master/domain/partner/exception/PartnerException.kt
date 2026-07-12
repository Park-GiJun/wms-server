package com.gijun.wms.master.domain.partner.exception

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.shared.exception.ErrorCode
import com.gijun.wms.shared.exception.WmsException

/**
 * 거래처 도메인 예외. 서브클래스가 [ErrorCode] 를 고정하고, 호출부는 컨텍스트(코드 등)만 넘긴다.
 */
sealed class PartnerException(
    code: ErrorCode,
    message: String = code.defaultMessage,
) : WmsException(code, message) {

    /** 거래처 코드 중복 — 409. */
    class DuplicatePartnerCodeException(code: String) :
        PartnerException(ErrorCode.CONFLICT, "이미 존재하는 거래처 코드입니다: $code")

    /** 대상 거래처 없음 — 404. */
    class PartnerNotFoundException(partnerId: Long) :
        PartnerException(ErrorCode.NOT_FOUND, "거래처를 찾을 수 없습니다: $partnerId")

    /** 이미 목표 상태 — 409 (중복 요청 감지). */
    class AlreadyInStatusException(target: PartnerStatus) :
        PartnerException(ErrorCode.CONFLICT, "이미 $target 상태입니다.")
}
