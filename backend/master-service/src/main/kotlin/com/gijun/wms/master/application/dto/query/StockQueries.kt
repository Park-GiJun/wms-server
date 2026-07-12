package com.gijun.wms.master.application.dto.query

/** 잔고 목록 조회 — skuId/locationId 필터는 각각 null 이면 전체. */
data class ListStockBalancesQuery(
    val skuId: Long?,
    val locationId: Long?,
)

/** 원장 이력 조회 — 전체 원장 덤프 방지를 위해 skuId/locationId 중 하나 이상 필수. */
data class ListStockMovementsQuery(
    val skuId: Long?,
    val locationId: Long?,
)
