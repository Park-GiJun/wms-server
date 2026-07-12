package com.gijun.wms.master.application.port.out.persistence.partner

import com.gijun.wms.master.domain.partner.PartnerModel

interface PartnerCommandPersistencePort {
    fun save(model: PartnerModel): PartnerModel
    fun findById(id: Long): PartnerModel?
}
