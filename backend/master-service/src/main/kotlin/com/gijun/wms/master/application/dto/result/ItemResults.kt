package com.gijun.wms.master.application.dto.result

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.OptionModel
import com.gijun.wms.master.domain.item.ProductModel
import com.gijun.wms.master.domain.item.SkuModel

/** 상품 요약(목록/수정 반환용) — SKU 미포함. */
data class ProductResult(
    val id: Long,
    val code: String,
    val name: String,
    val largeCategory: String,
    val mediumCategory: String?,
    val smallCategory: String?,
    val status: ItemStatus,
) {
    companion object {
        fun from(model: ProductModel): ProductResult = ProductResult(
            id = requireNotNull(model.id) { "저장된 상품은 id 가 있어야 한다" },
            code = model.code,
            name = model.name,
            largeCategory = model.largeCategory,
            mediumCategory = model.mediumCategory,
            smallCategory = model.smallCategory,
            status = model.status,
        )
    }
}

/** 상품 상세(생성/단건 조회 반환용) — SKU 포함. */
data class ProductDetailResult(
    val product: ProductResult,
    val skus: List<SkuResult>,
) {
    companion object {
        fun from(product: ProductModel, skus: List<SkuModel>): ProductDetailResult = ProductDetailResult(
            product = ProductResult.from(product),
            skus = skus.map(SkuResult::from),
        )
    }
}

data class SkuResult(
    val id: Long,
    val productId: Long,
    val skuCode: String,
    val barcode: String?,
    val options: List<OptionModel>,
    val unit: String,
    val status: ItemStatus,
) {
    companion object {
        fun from(model: SkuModel): SkuResult = SkuResult(
            id = requireNotNull(model.id) { "저장된 SKU 는 id 가 있어야 한다" },
            productId = model.productId,
            skuCode = model.skuCode,
            barcode = model.barcode,
            options = model.options,
            unit = model.unit,
            status = model.status,
        )
    }
}
