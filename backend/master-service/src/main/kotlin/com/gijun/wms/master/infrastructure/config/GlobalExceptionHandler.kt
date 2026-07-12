package com.gijun.wms.master.infrastructure.config

import com.gijun.wms.shared.exception.ErrorCode
import com.gijun.wms.shared.exception.WmsException
import com.gijun.wms.shared.web.ApiResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

/** WmsException(도메인 예외)의 ErrorCode 를 HTTP 상태 + 공통 응답 봉투로 매핑한다. */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(WmsException::class)
    fun handleWms(e: WmsException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(e.code.status)
            .body(ApiResponse.fail(e.code.name, e.message))

    /** 본문 파싱 실패(잘못된 enum 값 등)는 500 이 아니라 400 으로. */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(ErrorCode.INVALID_INPUT.status)
            .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.name, "요청 본문을 해석할 수 없습니다."))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val message = e.bindingResult.fieldErrors.firstOrNull()
            ?.let { "${it.field}: ${it.defaultMessage}" }
            ?: ErrorCode.INVALID_INPUT.defaultMessage
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.status)
            .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.name, message))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error(e) { "unexpected error" }
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.status)
            .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR.name, ErrorCode.INTERNAL_ERROR.defaultMessage))
    }
}
