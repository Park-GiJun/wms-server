package com.gijun.wms.shared.event

import java.time.Instant

/**
 * **척추 이벤트.** master-service(재고원장)가 이동을 확정할 때마다 발행하는 append-only 사실 기록.
 * 모든 물리 이동(입고·적치·이동·피킹·패킹·출고·조정·실사)이 이 한 이벤트로 환원된다.
 *
 * 확장 규칙: **새 기능 = 이 이벤트를 발행(커맨드)하거나 구독(투영)하는 피처 서비스 추가** —
 * 척추(재고원장)는 건드리지 않는다. Kafka key = "{skuId}:{locationId}" 로 (SKU,로케이션) 단위 순서보장.
 *
 * TRANSFER 는 이벤트 2건으로 분해된다(출발지 -qty + 도착지 +qty, 같은 refType/refId 공유) —
 * 그래서 이 이벤트에 from/to 가 없다.
 *
 * 필드는 최소 골격이다 — lot/serial/uom/reason 등은 도메인 설계에 맞게 채운다.
 */
data class StockMovementEvent(
    val movementId: String,   // 멱등 키(consumer 는 idem:{movementId})
    val skuId: Long,          // SKU(master 소유 — 원장은 Product 를 모른다)
    val locationId: Long,     // 로케이션(master 소유)
    val type: MovementType,   // 이동 종류
    val qty: Long,            // 부호 있는 수량(+유입/-유출), 0 금지 — 원장은 부호까지 append
    val refType: String?,     // 유발 문서 종류 (예: INBOUND_ORDER / PICK_LIST) — 조정 등은 없을 수 있다
    val refId: String?,       // 유발 문서 id
    val seq: Long,            // (skuId, locationId) 쌍 안에서 단조 증가 — consumer 의 순서/멱등 판단 기준
    val occurredAt: Instant,
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
