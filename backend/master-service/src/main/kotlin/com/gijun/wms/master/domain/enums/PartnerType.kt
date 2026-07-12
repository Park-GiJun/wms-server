package com.gijun.wms.master.domain.enums

/**
 * 거래처 유형 — 입고(inbound)는 SUPPLIER, 출고(outbound)는 CUSTOMER 문서가 참조한다.
 * 양방향 거래처(공급도 하고 구매도 하는)는 BOTH.
 */
enum class PartnerType {
    SUPPLIER,   // 공급처(입고)
    CUSTOMER,   // 고객사(출고)
    BOTH,       // 양방향
}
