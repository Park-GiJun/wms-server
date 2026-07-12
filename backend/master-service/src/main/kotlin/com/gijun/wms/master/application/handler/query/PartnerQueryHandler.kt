package com.gijun.wms.master.application.handler.query

import com.gijun.wms.master.application.dto.query.GetPartnerQuery
import com.gijun.wms.master.application.dto.query.ListPartnersQuery
import com.gijun.wms.master.application.dto.result.PartnerResult
import com.gijun.wms.master.application.port.`in`.query.GetPartnerQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.ListPartnersQueryUseCase
import com.gijun.wms.master.application.port.out.persistence.partner.PartnerQueryPersistencePort
import com.gijun.wms.master.domain.partner.exception.PartnerException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartnerQueryHandler(
    private val partnerQueryPersistencePort: PartnerQueryPersistencePort,
) : GetPartnerQueryUseCase, ListPartnersQueryUseCase {

    @Transactional(readOnly = true)
    override fun getPartner(query: GetPartnerQuery): PartnerResult =
        PartnerResult.from(
            partnerQueryPersistencePort.findById(query.partnerId)
                ?: throw PartnerException.PartnerNotFoundException(query.partnerId),
        )

    @Transactional(readOnly = true)
    override fun listPartners(query: ListPartnersQuery): List<PartnerResult> =
        partnerQueryPersistencePort.findPartners(query.status, query.type)
            .map(PartnerResult::from)
}
