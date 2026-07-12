package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.ChangeProductStatusCommand
import com.gijun.wms.master.application.dto.result.ProductResult

interface ChangeProductStatusCommandUseCase {
    fun changeProductStatus(command: ChangeProductStatusCommand): ProductResult
}
