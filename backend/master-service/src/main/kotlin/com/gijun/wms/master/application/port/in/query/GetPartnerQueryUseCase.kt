package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.GetPartnerQuery
import com.gijun.wms.master.application.dto.result.PartnerResult

interface GetPartnerQueryUseCase {
    fun getPartner(query: GetPartnerQuery): PartnerResult
}
