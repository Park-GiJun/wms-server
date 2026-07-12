package com.gijun.wms.master.domain.stock

import com.gijun.wms.shared.event.MovementType
import java.time.Instant

/**
 * 재고원장 레코드 — 물리 이동 1건의 append-only 사실 기록.
 * 생성 후 수정·삭제되지 않는다. 정정은 역방향 movement 를 추가한다.
 * 새 레코드는 반드시 [StockBalanceModel.apply] 를 통해서만 만들어진다(단일 라이터).
 */
data class StockMovementModel(
    val id: Long?,
    val skuId: Long,
    val locationId: Long,
    val type: MovementType,
    val qty: Long,            // 부호 있는 수량(+유입/-유출), 0 금지
    val refType: String?,     // 유발 문서 종류(INBOUND_ORDER 등) — 조정 등은 없을 수 있다
    val refId: String?,
    val seq: Long,            // (skuId, locationId) 쌍 안에서 단조 증가
    val occurredAt: Instant,
)
