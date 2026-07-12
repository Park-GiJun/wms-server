package com.gijun.wms.master.domain.enums

/**
 * 거래처 마스터 상태. 입출고 문서가 참조하는 마스터라 삭제 대신 INACTIVE 전환(soft delete)만 허용한다.
 */
enum class PartnerStatus {
    ACTIVE, INACTIVE
}
