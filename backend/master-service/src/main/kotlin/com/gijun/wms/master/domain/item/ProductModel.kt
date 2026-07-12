package com.gijun.wms.master.domain.item

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.exception.ItemException
import java.time.Instant

/**
 * 상품 — SKU 를 묶는 카탈로그/집계 단위. 재고를 직접 갖지 않는다(재고의 주인은 항상 SKU).
 * 옵션 없는 상품도 SKU 1개를 강제 생성해 원장이 아는 ID 를 skuId 하나로 유지한다.
 */
data class ProductModel(
    val id: Long?,
    val code: String,
    val name: String,
    val largeCategory: String,
    val mediumCategory: String?,
    val smallCategory: String?,
    val status: ItemStatus,
    val createdAt: Instant?,
    val modifiedAt: Instant?,
) {
    fun update(
        name: String,
        largeCategory: String,
        mediumCategory: String?,
        smallCategory: String?,
    ): ProductModel = copy(
        name = name,
        largeCategory = largeCategory,
        mediumCategory = mediumCategory,
        smallCategory = smallCategory,
        modifiedAt = Instant.now(),
    )

    /** ACTIVE ↔ INACTIVE 전환. 같은 상태로의 전환은 거부한다(중복 요청 감지). */
    fun changeStatus(target: ItemStatus): ProductModel {
        if (status == target) {
            throw ItemException.AlreadyInStatusException(target)
        }
        return copy(status = target, modifiedAt = Instant.now())
    }

    companion object {
        fun create(
            code: String,
            name: String,
            largeCategory: String,
            mediumCategory: String?,
            smallCategory: String?,
        ): ProductModel = ProductModel(
            id = null,
            code = code,
            name = name,
            largeCategory = largeCategory,
            mediumCategory = mediumCategory,
            smallCategory = smallCategory,
            status = ItemStatus.ACTIVE,
            createdAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
    }
}
