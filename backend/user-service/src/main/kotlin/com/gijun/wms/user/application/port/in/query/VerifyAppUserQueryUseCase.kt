package com.gijun.wms.user.application.port.`in`.query

import com.gijun.wms.user.application.dto.query.VerifyAppUserQuery

interface VerifyAppUserQueryUseCase {
    fun verifyAppUser(query : VerifyAppUserQuery)
}