package com.gijun.wms.master.infrastructure.adapter.`in`.item.web

import com.gijun.wms.master.application.dto.command.NewSkuData
import com.gijun.wms.master.application.dto.result.ProductDetailResult
import com.gijun.wms.master.application.dto.result.ProductResult
import com.gijun.wms.master.application.dto.result.SkuResult
import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.OptionModel
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateProductRequest(
    @field:NotBlank @field:Size(max = 50)
    val code: String,
    @field:NotBlank @field:Size(max = 255)
    val name: String,
    @field:NotBlank @field:Size(max = 100)
    val largeCategory: String,
    @field:Size(max = 100)
    val mediumCategory: String? = null,
    @field:Size(max = 100)
    val smallCategory: String? = null,
    // 재고는 SKU 단위 — 옵션 없는 상품도 SKU 1개는 필수.
    @field:NotEmpty @field:Valid
    val skus: List<NewSkuRequest>,
)

data class NewSkuRequest(
    @field:NotBlank @field:Size(max = 50)
    val skuCode: String,
    @field:Size(max = 50)
    val barcode: String? = null,
    @field:Valid
    val options: List<OptionRequest> = emptyList(),
    @field:NotBlank @field:Size(max = 20)
    val unit: String,
) {
    fun toData(): NewSkuData = NewSkuData(
        skuCode = skuCode,
        barcode = barcode,
        options = options.map { OptionModel(it.optionName, it.optionValue) },
        unit = unit,
    )
}

data class OptionRequest(
    @field:NotBlank @field:Size(max = 100)
    val optionName: String,
    @field:Size(max = 255)
    val optionValue: String? = null,
)

data class UpdateProductRequest(
    @field:NotBlank @field:Size(max = 255)
    val name: String,
    @field:NotBlank @field:Size(max = 100)
    val largeCategory: String,
    @field:Size(max = 100)
    val mediumCategory: String? = null,
    @field:Size(max = 100)
    val smallCategory: String? = null,
)

data class UpdateSkuRequest(
    @field:Size(max = 50)
    val barcode: String? = null,
    @field:Valid
    val options: List<OptionRequest> = emptyList(),
    @field:NotBlank @field:Size(max = 20)
    val unit: String,
)

/** 활성/비활성 전환 요청 — 상품/SKU 공용. */
data class ChangeStatusRequest(
    val status: ItemStatus,
)

data class ProductResponse(
    val id: Long,
    val code: String,
    val name: String,
    val largeCategory: String,
    val mediumCategory: String?,
    val smallCategory: String?,
    val status: ItemStatus,
) {
    companion object {
        fun from(result: ProductResult): ProductResponse = ProductResponse(
            id = result.id,
            code = result.code,
            name = result.name,
            largeCategory = result.largeCategory,
            mediumCategory = result.mediumCategory,
            smallCategory = result.smallCategory,
            status = result.status,
        )
    }
}

data class ProductDetailResponse(
    val product: ProductResponse,
    val skus: List<SkuResponse>,
) {
    companion object {
        fun from(result: ProductDetailResult): ProductDetailResponse = ProductDetailResponse(
            product = ProductResponse.from(result.product),
            skus = result.skus.map(SkuResponse::from),
        )
    }
}

data class SkuResponse(
    val id: Long,
    val productId: Long,
    val skuCode: String,
    val barcode: String?,
    val options: List<OptionResponse>,
    val unit: String,
    val status: ItemStatus,
) {
    companion object {
        fun from(result: SkuResult): SkuResponse = SkuResponse(
            id = result.id,
            productId = result.productId,
            skuCode = result.skuCode,
            barcode = result.barcode,
            options = result.options.map { OptionResponse(it.optionName, it.optionValue) },
            unit = result.unit,
            status = result.status,
        )
    }
}

data class OptionResponse(
    val optionName: String,
    val optionValue: String?,
)
