package com.gijun.wms.master.application.dto.command

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.OptionModel

/** 상품 생성 — 재고는 SKU 단위이므로 최소 1개의 SKU 를 함께 생성한다. */
data class CreateProductCommand(
    val code: String,
    val name: String,
    val largeCategory: String,
    val mediumCategory: String?,
    val smallCategory: String?,
    val skus: List<NewSkuData>,
)

/** 상품 생성/SKU 추가 시 넘기는 신규 SKU 데이터. */
data class NewSkuData(
    val skuCode: String,
    val barcode: String?,
    val options: List<OptionModel>,
    val unit: String,
)

/** 상품 기본 정보 수정 — code 는 비즈니스 키라 변경 불가. */
data class UpdateProductCommand(
    val productId: Long,
    val name: String,
    val largeCategory: String,
    val mediumCategory: String?,
    val smallCategory: String?,
)

/** 상품 활성/비활성 전환 — 마스터는 삭제 대신 INACTIVE. */
data class ChangeProductStatusCommand(
    val productId: Long,
    val status: ItemStatus,
)

/** 기존 상품에 SKU 추가 — ACTIVE 상품에만 가능. */
data class AddSkuCommand(
    val productId: Long,
    val sku: NewSkuData,
)

/** SKU 수정 — skuCode 는 비즈니스 키라 변경 불가. */
data class UpdateSkuCommand(
    val skuId: Long,
    val barcode: String?,
    val options: List<OptionModel>,
    val unit: String,
)

/** SKU 활성/비활성 전환. */
data class ChangeSkuStatusCommand(
    val skuId: Long,
    val status: ItemStatus,
)
