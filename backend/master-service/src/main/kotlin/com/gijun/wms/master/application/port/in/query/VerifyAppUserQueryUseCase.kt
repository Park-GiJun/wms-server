package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.VerifyAppUserQuery

interface VerifyAppUserQueryUseCase {
    fun verifyAppUser(query : VerifyAppUserQuery)
}