package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.ChangeSkuStatusCommand
import com.gijun.wms.master.application.dto.result.SkuResult

interface ChangeSkuStatusCommandUseCase {
    fun changeSkuStatus(command: ChangeSkuStatusCommand): SkuResult
}
