package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.ListProductsQuery
import com.gijun.wms.master.application.dto.result.ProductResult

interface ListProductsQueryUseCase {
    fun listProducts(query: ListProductsQuery): List<ProductResult>
}
