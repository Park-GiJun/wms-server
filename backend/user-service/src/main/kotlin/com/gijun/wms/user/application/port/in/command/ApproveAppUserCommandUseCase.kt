package com.gijun.wms.user.application.port.`in`.command

import com.gijun.wms.user.application.dto.command.ApproveAppUserCommand
import com.gijun.wms.user.application.dto.result.AppUserResult

interface ApproveAppUserCommandUseCase {
    fun approve(command: ApproveAppUserCommand): AppUserResult
}
