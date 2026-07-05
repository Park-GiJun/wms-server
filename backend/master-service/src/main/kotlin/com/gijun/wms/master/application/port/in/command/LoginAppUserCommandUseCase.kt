package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.LoginAppUserCommand
import com.gijun.wms.master.application.dto.result.TokenResult

interface LoginAppUserCommandUseCase {
    fun login(command: LoginAppUserCommand): TokenResult
}
