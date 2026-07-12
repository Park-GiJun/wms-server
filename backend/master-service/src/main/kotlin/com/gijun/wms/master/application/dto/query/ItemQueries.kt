package com.gijun.wms.master.application.dto.query

import com.gijun.wms.master.domain.enums.ItemStatus

/** 상품 단건 조회(SKU 포함). */
data class GetProductQuery(
    val productId: Long,
)

/** 상품 목록 조회 — status 가 null 이면 전체. */
data class ListProductsQuery(
    val status: ItemStatus?,
)

/** SKU 단건 조회 — skuCode 또는 barcode 중 정확히 하나로 찾는다(바코드 스캔 대응). */
data class LookupSkuQuery(
    val skuCode: String?,
    val barcode: String?,
)
