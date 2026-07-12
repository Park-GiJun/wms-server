package com.gijun.wms.master.application.port.out.persistence.partner

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import com.gijun.wms.master.domain.partner.PartnerModel

interface PartnerQueryPersistencePort {
    fun existsByCode(code: String): Boolean
    fun findById(id: Long): PartnerModel?
    fun findPartners(status: PartnerStatus?, type: PartnerType?): List<PartnerModel>
}
