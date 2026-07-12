package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.UpdateProductCommand
import com.gijun.wms.master.application.dto.result.ProductResult

interface UpdateProductCommandUseCase {
    fun updateProduct(command: UpdateProductCommand): ProductResult
}
