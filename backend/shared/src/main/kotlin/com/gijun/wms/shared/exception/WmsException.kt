package com.gijun.wms.shared.exception

/**
 * 모든 비즈니스 예외의 공통 상위 타입. 각 서비스의 도메인 sealed 예외가 이 타입을 매핑 대상으로 삼고,
 * config 의 예외 핸들러가 [code] 로 HTTP 응답을 결정한다.
 */
open class WmsException(
    val code: ErrorCode,
    override val message: String = code.defaultMessage,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
