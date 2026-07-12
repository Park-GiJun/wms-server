package com.gijun.wms.master.infrastructure.adapter.out.persistence.stock

import com.gijun.wms.master.domain.stock.StockMovementModel
import com.gijun.wms.shared.event.MovementType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * stock_movement 테이블 매핑(V6__stock_ledger.sql). append-only 라 전 필드 val — UPDATE 없음.
 * 도메인 모델과 1:1 변환만 담당, 로직 없음.
 */
@Entity
@Table(name = "stock_movement")
class StockMovementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "sku_id", nullable = false)
    val skuId: Long,

    @Column(name = "location_id", nullable = false)
    val locationId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    val type: MovementType,

    @Column(nullable = false)
    val qty: Long,

    @Column(name = "ref_type", length = 30)
    val refType: String?,

    @Column(name = "ref_id", length = 100)
    val refId: String?,

    @Column(nullable = false)
    val seq: Long,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: Instant,
) {
    fun toModel(): StockMovementModel = StockMovementModel(
        id = id,
        skuId = skuId,
        locationId = locationId,
        type = type,
        qty = qty,
        refType = refType,
        refId = refId,
        seq = seq,
        occurredAt = occurredAt,
    )

    companion object {
        fun from(model: StockMovementModel): StockMovementJpaEntity = StockMovementJpaEntity(
            id = model.id,
            skuId = model.skuId,
            locationId = model.locationId,
            type = model.type,
            qty = model.qty,
            refType = model.refType,
            refId = model.refId,
            seq = model.seq,
            occurredAt = model.occurredAt,
        )
    }
}
