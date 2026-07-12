package com.gijun.wms.master.infrastructure.adapter.out.persistence.item

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.OptionModel
import com.gijun.wms.master.domain.item.SkuModel
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table
import java.time.Instant

/**
 * sku 테이블 매핑(V3__product_sku.sql). 옵션은 sku_option 컬렉션 테이블로 뺀다.
 * product 와는 FK(productId) 값만 들고 연관관계 매핑을 하지 않는다(어그리게이트 경계 분리).
 */
@Entity
@Table(name = "sku")
class SkuJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "sku_code", nullable = false, unique = true, length = 50)
    val skuCode: String,

    @Column(unique = true, length = 50)
    val barcode: String? = null,

    // 옵션 수가 적어(색상/사이즈 수준) EAGER 로 함께 읽는다.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sku_option", joinColumns = [JoinColumn(name = "sku_id")])
    @OrderColumn(name = "option_order")
    val options: List<SkuOptionEmbeddable>,

    @Column(nullable = false, length = 20)
    val unit: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: ItemStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "modified_at", nullable = false)
    val modifiedAt: Instant,
) {
    fun toModel(): SkuModel = SkuModel(
        id = id,
        productId = productId,
        skuCode = skuCode,
        barcode = barcode,
        options = options.map { OptionModel(it.optionName, it.optionValue) },
        unit = unit,
        status = status,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

    companion object {
        fun from(model: SkuModel): SkuJpaEntity = SkuJpaEntity(
            id = model.id,
            productId = model.productId,
            skuCode = model.skuCode,
            barcode = model.barcode,
            options = model.options.map { SkuOptionEmbeddable(it.optionName, it.optionValue) },
            unit = model.unit,
            status = model.status,
            createdAt = model.createdAt ?: Instant.now(),
            modifiedAt = model.modifiedAt ?: Instant.now(),
        )
    }
}

@Embeddable
class SkuOptionEmbeddable(
    @Column(name = "option_name", nullable = false, length = 100)
    val optionName: String,

    @Column(name = "option_value")
    val optionValue: String? = null,
)
