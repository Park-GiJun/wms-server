package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.GetProductQuery
import com.gijun.wms.master.application.dto.result.ProductDetailResult

interface GetProductQueryUseCase {
    fun getProduct(query: GetProductQuery): ProductDetailResult
}
