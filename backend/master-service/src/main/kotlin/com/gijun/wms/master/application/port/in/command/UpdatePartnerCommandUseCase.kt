package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.UpdatePartnerCommand
import com.gijun.wms.master.application.dto.result.PartnerResult

interface UpdatePartnerCommandUseCase {
    fun updatePartner(command: UpdatePartnerCommand): PartnerResult
}
