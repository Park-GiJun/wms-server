package com.gijun.wms.master.application.port.`in`.command

import com.gijun.wms.master.application.dto.command.RecordTransferCommand
import com.gijun.wms.master.application.dto.result.TransferResult

interface RecordTransferCommandUseCase {
    fun transfer(command: RecordTransferCommand): TransferResult
}
