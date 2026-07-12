package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.RecordReceiptCommand
import com.gijun.wms.master.application.dto.result.StockMovementResult

interface RecordReceiptCommandUseCase {
    fun receive(command: RecordReceiptCommand): StockMovementResult
}
