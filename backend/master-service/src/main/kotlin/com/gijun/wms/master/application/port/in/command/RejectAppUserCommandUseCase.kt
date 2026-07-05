package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.RejectAppUserCommand
import com.gijun.wms.master.application.dto.result.AppUserResult

interface RejectAppUserCommandUseCase {
    fun reject(command: RejectAppUserCommand): AppUserResult
}
