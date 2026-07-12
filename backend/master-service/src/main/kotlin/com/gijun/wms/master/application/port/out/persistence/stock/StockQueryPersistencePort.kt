package com.gijun.wms.master.application.port.out.persistence.stock

import com.gijun.wms.master.domain.stock.StockBalanceModel
import com.gijun.wms.master.domain.stock.StockMovementModel

/** 재고원장 조회 영속 포트 — 락 없는 읽기 전용. */
interface StockQueryPersistencePort {
    fun findBalances(skuId: Long?, locationId: Long?): List<StockBalanceModel>

    /** 원장 이력 — 최신순(id 내림차순). 필터 없는 전체 조회는 지원하지 않는다(호출부가 보장). */
    fun findMovements(skuId: Long?, locationId: Long?): List<StockMovementModel>
}
