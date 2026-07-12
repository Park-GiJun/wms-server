package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.RecordAdjustmentCommand
import com.gijun.wms.master.application.dto.result.StockMovementResult

interface RecordAdjustmentCommandUseCase {
    fun adjust(command: RecordAdjustmentCommand): StockMovementResult
}
