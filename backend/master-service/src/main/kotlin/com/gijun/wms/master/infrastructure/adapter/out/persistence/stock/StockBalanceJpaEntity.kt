package com.gijun.wms.master.infrastructure.adapter.out.persistence.stock

import com.gijun.wms.master.domain.stock.StockBalanceModel
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant

/** stock_balance 복합 PK — (sku_id, location_id) = 단일 라이터의 락 단위. */
@Embeddable
data class StockBalanceId(
    @Column(name = "sku_id")
    val skuId: Long = 0,

    @Column(name = "location_id")
    val locationId: Long = 0,
) : Serializable

/**
 * stock_balance 테이블 매핑(V6__stock_ledger.sql). 도메인 모델과 1:1 변환만 담당 — 로직 없음.
 * qty >= 0 불변식은 도메인이, CHECK 제약은 DB 가 이중으로 지킨다.
 */
@Entity
@Table(name = "stock_balance")
class StockBalanceJpaEntity(
    @EmbeddedId
    val id: StockBalanceId,

    @Column(nullable = false)
    var qty: Long,

    @Column(name = "last_seq", nullable = false)
    var lastSeq: Long,

    @Column(name = "modified_at", nullable = false)
    var modifiedAt: Instant,
) {
    fun toModel(): StockBalanceModel = StockBalanceModel(
        skuId = id.skuId,
        locationId = id.locationId,
        qty = qty,
        lastSeq = lastSeq,
        modifiedAt = modifiedAt,
    )

    companion object {
        fun from(model: StockBalanceModel): StockBalanceJpaEntity = StockBalanceJpaEntity(
            id = StockBalanceId(skuId = model.skuId, locationId = model.locationId),
            qty = model.qty,
            lastSeq = model.lastSeq,
            modifiedAt = model.modifiedAt ?: Instant.now(),
        )
    }
}
