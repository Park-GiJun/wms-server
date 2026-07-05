package com.gijun.wms.shared.web

/**
 * 서비스 공통 REST 응답 봉투. 성공은 [data], 실패는 [error] 만 채운다.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
) {
    companion object {
        fun <T> ok(data: T): ApiResponse<T> = ApiResponse(success = true, data = data)
        fun fail(code: String, message: String): ApiResponse<Nothing> =
            ApiResponse(success = false, error = ApiError(code, message))
    }
}

data class ApiError(
    val code: String,
    val message: String,
)
