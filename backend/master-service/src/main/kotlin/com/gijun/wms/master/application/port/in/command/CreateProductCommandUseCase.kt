package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.CreateProductCommand
import com.gijun.wms.master.application.dto.result.ProductDetailResult

interface CreateProductCommandUseCase {
    fun createProduct(command: CreateProductCommand): ProductDetailResult
}
