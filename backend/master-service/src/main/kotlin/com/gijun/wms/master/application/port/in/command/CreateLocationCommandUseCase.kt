package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.CreateLocationCommand
import com.gijun.wms.master.application.dto.result.LocationResult

interface CreateLocationCommandUseCase {
    fun createLocation(command: CreateLocationCommand): LocationResult
}
