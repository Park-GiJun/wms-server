package com.gijun.wms.master.application.handler.command

import com.gijun.wms.master.application.dto.command.ChangePartnerStatusCommand
import com.gijun.wms.master.application.dto.command.CreatePartnerCommand
import com.gijun.wms.master.application.dto.command.UpdatePartnerCommand
import com.gijun.wms.master.application.dto.result.PartnerResult
import com.gijun.wms.master.application.port.`in`.command.ChangePartnerStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.CreatePartnerCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdatePartnerCommandUseCase
import com.gijun.wms.master.application.port.out.persistence.partner.PartnerCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.partner.PartnerQueryPersistencePort
import com.gijun.wms.master.domain.partner.PartnerModel
import com.gijun.wms.master.domain.partner.exception.PartnerException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartnerCommandHandler(
    private val partnerCommandPersistencePort: PartnerCommandPersistencePort,
    private val partnerQueryPersistencePort: PartnerQueryPersistencePort,
) : CreatePartnerCommandUseCase, UpdatePartnerCommandUseCase, ChangePartnerStatusCommandUseCase {

    @Transactional
    override fun createPartner(command: CreatePartnerCommand): PartnerResult {
        if (partnerQueryPersistencePort.existsByCode(command.code)) {
            throw PartnerException.DuplicatePartnerCodeException(command.code)
        }
        return PartnerResult.from(
            partnerCommandPersistencePort.save(
                PartnerModel.create(code = command.code, name = command.name, type = command.type),
            ),
        )
    }

    @Transactional
    override fun updatePartner(command: UpdatePartnerCommand): PartnerResult {
        val partner = loadPartner(command.partnerId)
        return PartnerResult.from(
            partnerCommandPersistencePort.save(
                partner.update(name = command.name, type = command.type),
            ),
        )
    }

    /** 상태 전이 검증(같은 상태 거부)은 도메인(changeStatus)이 한다. */
    @Transactional
    override fun changePartnerStatus(command: ChangePartnerStatusCommand): PartnerResult {
        val partner = loadPartner(command.partnerId)
        return PartnerResult.from(
            partnerCommandPersistencePort.save(partner.changeStatus(command.status)),
        )
    }

    private fun loadPartner(partnerId: Long): PartnerModel =
        partnerCommandPersistencePort.findById(partnerId)
            ?: throw PartnerException.PartnerNotFoundException(partnerId)
}
