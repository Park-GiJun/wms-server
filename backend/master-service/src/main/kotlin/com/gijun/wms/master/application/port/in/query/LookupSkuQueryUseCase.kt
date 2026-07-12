package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.LookupSkuQuery
import com.gijun.wms.master.application.dto.result.SkuResult

interface LookupSkuQueryUseCase {
    fun lookupSku(query: LookupSkuQuery): SkuResult
}
