package com.gijun.wms.master.infrastructure.adapter.out.persistence.stock

import org.springframework.data.jpa.repository.JpaRepository

/** 원장 레코드는 append-only — INSERT 와 조회만 쓴다(UPDATE/DELETE 금지). 이력은 최신순(id 내림차순). */
interface StockMovementJpaRepository : JpaRepository<StockMovementJpaEntity, Long> {
    fun findBySkuIdAndLocationIdOrderByIdDesc(skuId: Long, locationId: Long): List<StockMovementJpaEntity>
    fun findBySkuIdOrderByIdDesc(skuId: Long): List<StockMovementJpaEntity>
    fun findByLocationIdOrderByIdDesc(locationId: Long): List<StockMovementJpaEntity>
}
