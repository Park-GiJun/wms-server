package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.AddSkuCommand
import com.gijun.wms.master.application.dto.result.SkuResult

interface AddSkuCommandUseCase {
    fun addSku(command: AddSkuCommand): SkuResult
}
