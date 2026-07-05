package com.gijun.wms.user.application.port.`in`.command

import com.gijun.wms.user.application.dto.command.RegisterAppUserCommand
import com.gijun.wms.user.application.dto.result.AppUserResult

interface RegisterAppUserCommandUseCase {
    fun register(command: RegisterAppUserCommand): AppUserResult
}
