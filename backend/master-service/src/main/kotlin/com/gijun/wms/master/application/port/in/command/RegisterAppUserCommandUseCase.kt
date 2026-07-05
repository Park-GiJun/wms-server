package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.RegisterAppUserCommand
import com.gijun.wms.master.application.dto.result.AppUserResult

interface RegisterAppUserCommandUseCase {
    fun register(command: RegisterAppUserCommand): AppUserResult
}
