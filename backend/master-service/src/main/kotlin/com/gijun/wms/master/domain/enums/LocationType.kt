package com.gijun.wms.master.domain.enums

/**
 * 로케이션 용도. StockMovementEvent 의 이동 타입(입고→적치→피킹→패킹→출고)이 오가는 물리 구역과 대응한다.
 */
enum class LocationType {
    RECEIVING,   // 입고장
    STORAGE,     // 보관(적치)
    PICKING,     // 피킹
    PACKING,     // 패킹
    SHIPPING,    // 출하장
}
