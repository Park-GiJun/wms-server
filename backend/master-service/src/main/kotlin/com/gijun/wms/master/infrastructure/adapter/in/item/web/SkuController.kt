package com.gijun.wms.master.infrastructure.adapter.`in`.item.web

import com.gijun.wms.master.application.dto.command.ChangeSkuStatusCommand
import com.gijun.wms.master.application.dto.command.UpdateSkuCommand
import com.gijun.wms.master.application.dto.query.LookupSkuQuery
import com.gijun.wms.master.application.port.`in`.command.ChangeSkuStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdateSkuCommandUseCase
import com.gijun.wms.master.application.port.`in`.query.LookupSkuQueryUseCase
import com.gijun.wms.master.domain.item.OptionModel
import com.gijun.wms.shared.web.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * SKU 마스터 API — 생성은 상품 하위(/api/products/{id}/skus)에서, 수정/조회는 여기서.
 */
@RestController
@RequestMapping("/api/skus")
class SkuController(
    private val updateSkuCommandUseCase: UpdateSkuCommandUseCase,
    private val changeSkuStatusCommandUseCase: ChangeSkuStatusCommandUseCase,
    private val lookupSkuQueryUseCase: LookupSkuQueryUseCase,
) {

    /** SKU 단건 조회 — skuCode 또는 barcode 중 정확히 하나로(바코드 스캔 대응). */
    @GetMapping
    fun lookup(
        @RequestParam(required = false) skuCode: String?,
        @RequestParam(required = false) barcode: String?,
    ): ApiResponse<SkuResponse> {
        val result = lookupSkuQueryUseCase.lookupSku(LookupSkuQuery(skuCode, barcode))
        return ApiResponse.ok(SkuResponse.from(result))
    }

    /** SKU 수정 — skuCode 는 비즈니스 키라 변경 불가, 바코드 중복 시 409. */
    @PutMapping("/{skuId}")
    fun update(
        @PathVariable skuId: Long,
        @Valid @RequestBody request: UpdateSkuRequest,
    ): ApiResponse<SkuResponse> {
        val result = updateSkuCommandUseCase.updateSku(
            UpdateSkuCommand(
                skuId = skuId,
                barcode = request.barcode,
                options = request.options.map { OptionModel(it.optionName, it.optionValue) },
                unit = request.unit,
            ),
        )
        return ApiResponse.ok(SkuResponse.from(result))
    }

    /** 활성/비활성 전환 — 같은 상태로의 전환은 409. */
    @PatchMapping("/{skuId}/status")
    fun changeStatus(
        @PathVariable skuId: Long,
        @Valid @RequestBody request: ChangeStatusRequest,
    ): ApiResponse<SkuResponse> {
        val result = changeSkuStatusCommandUseCase.changeSkuStatus(
            ChangeSkuStatusCommand(skuId, request.status),
        )
        return ApiResponse.ok(SkuResponse.from(result))
    }
}
