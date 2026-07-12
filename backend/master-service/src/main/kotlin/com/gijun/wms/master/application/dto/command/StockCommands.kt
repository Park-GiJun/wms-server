package com.gijun.wms.master.application.dto.command

data class RecordReceiptCommand(
    val skuId: Long,
    val locationId: Long,
    val qty: Long,
    val refType: String?,
    val refId: String?
)

data class RecordTransferCommand(
    val skuId: Long,
    val fromLocationId: Long,
    val toLocationId: Long,
    val qty: Long,
    val refType: String?,
    val refId: String?
)

data class RecordAdjustmentCommand(
    val skuId: Long,
    val locationId: Long,
    val qtyDelta: Long,
    val refType: String?,
    val refId: String?
)
