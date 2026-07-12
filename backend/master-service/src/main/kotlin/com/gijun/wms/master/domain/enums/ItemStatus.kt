package com.gijun.wms.master.domain.enums

/**
 * 상품/SKU 마스터 상태. 재고원장이 참조하는 마스터라 삭제 대신 INACTIVE 전환(soft delete)만 허용한다.
 */
enum class ItemStatus {
    ACTIVE, INACTIVE
}
