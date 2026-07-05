package com.gijun.wms.user.application.port.`in`.command

import com.gijun.wms.user.application.dto.command.LoginAppUserCommand
import com.gijun.wms.user.application.dto.result.TokenResult

interface LoginAppUserCommandUseCase {
    fun login(command: LoginAppUserCommand): TokenResult
}
