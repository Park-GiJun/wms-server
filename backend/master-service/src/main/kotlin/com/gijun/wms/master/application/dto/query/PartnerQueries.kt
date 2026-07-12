package com.gijun.wms.master.application.dto.query

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType

/** 거래처 단건 조회. */
data class GetPartnerQuery(
    val partnerId: Long,
)

/** 거래처 목록 조회 — status/type 필터는 각각 null 이면 전체. */
data class ListPartnersQuery(
    val status: PartnerStatus?,
    val type: PartnerType?,
)
