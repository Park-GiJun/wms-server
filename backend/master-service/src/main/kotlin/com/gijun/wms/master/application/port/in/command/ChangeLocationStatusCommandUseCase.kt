package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.ChangeLocationStatusCommand
import com.gijun.wms.master.application.dto.result.LocationResult

interface ChangeLocationStatusCommandUseCase {
    fun changeLocationStatus(command: ChangeLocationStatusCommand): LocationResult
}
