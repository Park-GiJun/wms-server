package com.gijun.wms.master.domain.item

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.exception.ItemException
import java.time.Instant

/**
 * SKU — 재고의 최소 단위이자 재고원장(StockMovement/StockBalance)이 참조하는 유일한 품목 식별자.
 * 상품(Product)은 원장에 등장하지 않는다. qty 단위는 항상 [unit](최소 단위) 고정.
 */
data class SkuModel(
    val id: Long?,
    val productId: Long,
    val skuCode: String,
    val barcode: String?,
    val options: List<OptionModel>,
    val unit: String,
    val status: ItemStatus,
    val createdAt: Instant?,
    val modifiedAt: Instant?,
) {
    fun update(barcode: String?, options: List<OptionModel>, unit: String): SkuModel = copy(
        barcode = barcode,
        options = options,
        unit = unit,
        modifiedAt = Instant.now(),
    )

    /** ACTIVE ↔ INACTIVE 전환. 같은 상태로의 전환은 거부한다(중복 요청 감지). */
    fun changeStatus(target: ItemStatus): SkuModel {
        if (status == target) {
            throw ItemException.AlreadyInStatusException(target)
        }
        return copy(status = target, modifiedAt = Instant.now())
    }

    companion object {
        fun create(
            productId: Long,
            skuCode: String,
            barcode: String?,
            options: List<OptionModel>,
            unit: String,
        ): SkuModel = SkuModel(
            id = null,
            productId = productId,
            skuCode = skuCode,
            barcode = barcode,
            options = options,
            unit = unit,
            status = ItemStatus.ACTIVE,
            createdAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
    }
}

/** SKU 옵션(색상/사이즈 등). 옵션 없는 상품의 SKU 는 빈 리스트. */
data class OptionModel(
    val optionName: String,
    val optionValue: String?,
)
