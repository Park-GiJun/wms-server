package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.ApproveAppUserCommand
import com.gijun.wms.master.application.dto.result.AppUserResult

interface ApproveAppUserCommandUseCase {
    fun approve(command: ApproveAppUserCommand): AppUserResult
}
