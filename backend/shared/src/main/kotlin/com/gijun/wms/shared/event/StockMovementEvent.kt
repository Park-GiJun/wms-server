package com.gijun.wms.shared.event

/**
 * **척추 이벤트.** master-service(재고원장)가 이동을 확정할 때마다 발행하는 append-only 사실 기록.
 * 모든 물리 이동(입고·적치·이동·피킹·패킹·출고·조정·실사)이 이 한 이벤트로 환원된다.
 *
 * 확장 규칙: **새 기능 = 이 이벤트를 발행(커맨드)하거나 구독(투영)하는 피처 서비스 추가** —
 * 척추(재고원장)는 건드리지 않는다. Kafka key = "{itemId}:{locationId}" 로 (품목,로케이션) 단위 순서보장.
 *
 * 필드는 최소 골격이다 — lot/serial/uom/reason 등은 도메인 설계에 맞게 채운다.
 */
data class StockMovementEvent(
    val movementId: String,   // 멱등 키(consumer 는 idem:{movementId})
    val itemId: String,       // 품목(master 소유)
    val locationId: String,   // 로케이션(master 소유)
    val type: MovementType,   // 이동 종류
    val qty: Long,            // 수량(부호는 type 이 결정 — 원장은 부호까지 append)
    val refType: String,      // 유발 문서 종류 (예: INBOUND_ORDER / PICK_LIST / ADJUSTMENT)
    val refId: String,        // 유발 문서 id
    val seq: Long,            // 원장 내 전역 증가 시퀀스
)

enum class MovementType {
    RECEIPT,     // 입고
    PUTAWAY,     // 적치
    TRANSFER,    // 로케이션 이동
    PICK,        // 피킹
    PACK,        // 패킹
    SHIP,        // 출고
    ADJUSTMENT,  // 재고 조정(실사 차이 등)
}
