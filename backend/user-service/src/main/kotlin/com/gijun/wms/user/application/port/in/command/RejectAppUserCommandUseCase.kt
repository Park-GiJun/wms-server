package com.gijun.wms.user.application.port.`in`.command

import com.gijun.wms.user.application.dto.command.RejectAppUserCommand
import com.gijun.wms.user.application.dto.result.AppUserResult

interface RejectAppUserCommandUseCase {
    fun reject(command: RejectAppUserCommand): AppUserResult
}
