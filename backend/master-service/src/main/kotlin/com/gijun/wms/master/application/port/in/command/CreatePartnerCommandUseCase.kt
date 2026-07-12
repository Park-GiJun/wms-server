package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.CreatePartnerCommand
import com.gijun.wms.master.application.dto.result.PartnerResult

interface CreatePartnerCommandUseCase {
    fun createPartner(command: CreatePartnerCommand): PartnerResult
}
