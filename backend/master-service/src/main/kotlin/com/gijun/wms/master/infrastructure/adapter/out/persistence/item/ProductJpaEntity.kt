package com.gijun.wms.master.infrastructure.adapter.out.persistence.item

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.ProductModel
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
 * product 테이블 매핑(V3__product_sku.sql). 도메인 모델과 1:1 변환만 담당 — 로직 없음.
 */
@Entity
@Table(name = "product")
class ProductJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    val code: String,

    @Column(nullable = false)
    val name: String,

    @Column(name = "large_category", nullable = false, length = 100)
    val largeCategory: String,

    @Column(name = "medium_category", length = 100)
    val mediumCategory: String? = null,

    @Column(name = "small_category", length = 100)
    val smallCategory: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: ItemStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "modified_at", nullable = false)
    val modifiedAt: Instant,
) {
    fun toModel(): ProductModel = ProductModel(
        id = id,
        code = code,
        name = name,
        largeCategory = largeCategory,
        mediumCategory = mediumCategory,
        smallCategory = smallCategory,
        status = status,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

    companion object {
        fun from(model: ProductModel): ProductJpaEntity = ProductJpaEntity(
            id = model.id,
            code = model.code,
            name = model.name,
            largeCategory = model.largeCategory,
            mediumCategory = model.mediumCategory,
            smallCategory = model.smallCategory,
            status = model.status,
            createdAt = model.createdAt ?: Instant.now(),
            modifiedAt = model.modifiedAt ?: Instant.now(),
        )
    }
}
