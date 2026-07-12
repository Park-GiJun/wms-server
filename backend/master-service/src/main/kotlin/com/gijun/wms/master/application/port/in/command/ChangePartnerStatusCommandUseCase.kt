package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.ChangePartnerStatusCommand
import com.gijun.wms.master.application.dto.result.PartnerResult

interface ChangePartnerStatusCommandUseCase {
    fun changePartnerStatus(command: ChangePartnerStatusCommand): PartnerResult
}
