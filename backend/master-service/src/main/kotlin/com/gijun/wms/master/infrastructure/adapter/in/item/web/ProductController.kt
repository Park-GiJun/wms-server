package com.gijun.wms.master.infrastructure.adapter.`in`.item.web

import com.gijun.wms.master.application.dto.command.AddSkuCommand
import com.gijun.wms.master.application.dto.command.ChangeProductStatusCommand
import com.gijun.wms.master.application.dto.command.CreateProductCommand
import com.gijun.wms.master.application.dto.command.UpdateProductCommand
import com.gijun.wms.master.application.dto.query.GetProductQuery
import com.gijun.wms.master.application.dto.query.ListProductsQuery
import com.gijun.wms.master.application.port.`in`.command.AddSkuCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.ChangeProductStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.CreateProductCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdateProductCommandUseCase
import com.gijun.wms.master.application.port.`in`.query.GetProductQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.ListProductsQueryUseCase
import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.shared.web.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 상품 마스터 API. gateway 인증 경로라 유효한 JWT 를 가진 사용자만 도달한다.
 * 마스터는 삭제 대신 status 전환(INACTIVE)만 제공한다 — 재고원장이 참조하기 때문.
 */
@RestController
@RequestMapping("/api/products")
class ProductController(
    private val createProductCommandUseCase: CreateProductCommandUseCase,
    private val updateProductCommandUseCase: UpdateProductCommandUseCase,
    private val changeProductStatusCommandUseCase: ChangeProductStatusCommandUseCase,
    private val addSkuCommandUseCase: AddSkuCommandUseCase,
    private val getProductQueryUseCase: GetProductQueryUseCase,
    private val listProductsQueryUseCase: ListProductsQueryUseCase,
) {

    /** 상품 생성 — SKU 최소 1개 포함(재고는 SKU 단위). 코드/SKU 코드/바코드 중복 시 409. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateProductRequest): ApiResponse<ProductDetailResponse> {
        val result = createProductCommandUseCase.createProduct(
            CreateProductCommand(
                code = request.code,
                name = request.name,
                largeCategory = request.largeCategory,
                mediumCategory = request.mediumCategory,
                smallCategory = request.smallCategory,
                skus = request.skus.map { it.toData() },
            ),
        )
        return ApiResponse.ok(ProductDetailResponse.from(result))
    }

    @GetMapping
    fun list(@RequestParam(required = false) status: ItemStatus?): ApiResponse<List<ProductResponse>> {
        val results = listProductsQueryUseCase.listProducts(ListProductsQuery(status))
        return ApiResponse.ok(results.map(ProductResponse::from))
    }

    @GetMapping("/{productId}")
    fun get(@PathVariable productId: Long): ApiResponse<ProductDetailResponse> {
        val result = getProductQueryUseCase.getProduct(GetProductQuery(productId))
        return ApiResponse.ok(ProductDetailResponse.from(result))
    }

    @PutMapping("/{productId}")
    fun update(
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductRequest,
    ): ApiResponse<ProductResponse> {
        val result = updateProductCommandUseCase.updateProduct(
            UpdateProductCommand(
                productId = productId,
                name = request.name,
                largeCategory = request.largeCategory,
                mediumCategory = request.mediumCategory,
                smallCategory = request.smallCategory,
            ),
        )
        return ApiResponse.ok(ProductResponse.from(result))
    }

    /** 활성/비활성 전환 — 같은 상태로의 전환은 409. */
    @PatchMapping("/{productId}/status")
    fun changeStatus(
        @PathVariable productId: Long,
        @Valid @RequestBody request: ChangeStatusRequest,
    ): ApiResponse<ProductResponse> {
        val result = changeProductStatusCommandUseCase.changeProductStatus(
            ChangeProductStatusCommand(productId, request.status),
        )
        return ApiResponse.ok(ProductResponse.from(result))
    }

    /** 기존 상품에 SKU 추가 — ACTIVE 상품에만 가능. */
    @PostMapping("/{productId}/skus")
    @ResponseStatus(HttpStatus.CREATED)
    fun addSku(
        @PathVariable productId: Long,
        @Valid @RequestBody request: NewSkuRequest,
    ): ApiResponse<SkuResponse> {
        val result = addSkuCommandUseCase.addSku(AddSkuCommand(productId, request.toData()))
        return ApiResponse.ok(SkuResponse.from(result))
    }
}
