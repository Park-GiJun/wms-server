package com.gijun.wms.master.application.port.out.persistence.stock

import com.gijun.wms.master.domain.stock.StockApplication
import com.gijun.wms.master.domain.stock.StockBalanceModel
import com.gijun.wms.master.domain.stock.StockMovementModel

/**
 * 재고원장 커맨드 영속 포트 — (skuId, locationId) 단일 라이터의 락 획득과 기록.
 * 같은 트랜잭션 안에서 [ensureAndLock] → 도메인 apply → [persist] 순서로 쓴다.
 */
interface StockCommandPersistencePort {

    /**
     * (skuId, locationId) 잔고 행을 없으면 만들고(qty 0) FOR UPDATE 로 잠근 뒤 반환한다.
     * "SELECT 후 INSERT" 는 없는 행에 락이 걸리지 않아 생성 경합이 나므로 INSERT-먼저가 규칙이다.
     */
    fun ensureAndLock(skuId: Long, locationId: Long): StockBalanceModel

    /** apply 결과(잔고 갱신 + movement INSERT)를 기록하고, id 가 채워진 movement 를 반환한다. */
    fun persist(application: StockApplication): StockMovementModel
}
