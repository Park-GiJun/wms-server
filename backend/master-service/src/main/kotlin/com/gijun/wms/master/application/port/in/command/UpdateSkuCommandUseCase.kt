package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.UpdateSkuCommand
import com.gijun.wms.master.application.dto.result.SkuResult

interface UpdateSkuCommandUseCase {
    fun updateSku(command: UpdateSkuCommand): SkuResult
}
