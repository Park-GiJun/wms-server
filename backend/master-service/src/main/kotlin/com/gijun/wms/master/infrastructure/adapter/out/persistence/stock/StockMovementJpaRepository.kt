package com.gijun.wms.master.infrastructure.adapter.out.persistence.stock

import org.springframework.data.jpa.repository.JpaRepository

/** 원장 레코드는 append-only — INSERT 와 조회만 쓴다(UPDATE/DELETE 금지). */
interface StockMovementJpaRepository : JpaRepository<StockMovementJpaEntity, Long>
