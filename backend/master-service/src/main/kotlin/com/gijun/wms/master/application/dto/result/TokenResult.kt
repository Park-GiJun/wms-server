package com.gijun.wms.master.application.dto.result

/** 로그인 성공 시 발급되는 액세스 토큰. */
data class TokenResult(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long,
)
