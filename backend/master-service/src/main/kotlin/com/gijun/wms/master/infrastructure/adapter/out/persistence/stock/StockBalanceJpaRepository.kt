package com.gijun.wms.master.infrastructure.adapter.out.persistence.stock

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StockBalanceJpaRepository : JpaRepository<StockBalanceJpaEntity, StockBalanceId> {

    // 복합 PK(@EmbeddedId) 내부 필드 탐색이라 property path 는 id.skuId / id.locationId.
    fun findByIdSkuId(skuId: Long): List<StockBalanceJpaEntity>
    fun findByIdLocationId(locationId: Long): List<StockBalanceJpaEntity>

    /** (sku, location) 잔고 행을 SELECT ... FOR UPDATE 로 잠근다 — 단일 라이터의 실체. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM StockBalanceJpaEntity b WHERE b.id = :id")
    fun findForUpdate(@Param("id") id: StockBalanceId): StockBalanceJpaEntity?

    /**
     * 잔고 행이 없으면 빈 행(qty 0)을 만든다. 경합 시 한쪽만 삽입되고 나머지는 조용히 통과 —
     * "SELECT 후 INSERT" 의 생성 경합(PK 충돌 → 트랜잭션 abort)을 피하기 위한 INSERT-먼저 규칙.
     */
    @Modifying
    @Query(
        value = """
            INSERT INTO stock_balance (sku_id, location_id, qty, last_seq, modified_at)
            VALUES (:skuId, :locationId, 0, 0, now())
            ON CONFLICT DO NOTHING
        """,
        nativeQuery = true,
    )
    fun insertIfAbsent(@Param("skuId") skuId: Long, @Param("locationId") locationId: Long): Int
}
