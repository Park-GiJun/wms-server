package com.gijun.wms.master.application.port.`in`.query

import com.gijun.wms.master.application.dto.query.ListPartnersQuery
import com.gijun.wms.master.application.dto.result.PartnerResult

interface ListPartnersQueryUseCase {
    fun listPartners(query: ListPartnersQuery): List<PartnerResult>
}
