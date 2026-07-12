package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.UpdateLocationCommand
import com.gijun.wms.master.application.dto.result.LocationResult

interface UpdateLocationCommandUseCase {
    fun updateLocation(command: UpdateLocationCommand): LocationResult
}
