package com.gijun.wms.master.infrastructure.adapter.`in`.partner.web

import com.gijun.wms.master.application.dto.command.ChangePartnerStatusCommand
import com.gijun.wms.master.application.dto.command.CreatePartnerCommand
import com.gijun.wms.master.application.dto.command.UpdatePartnerCommand
import com.gijun.wms.master.application.dto.query.GetPartnerQuery
import com.gijun.wms.master.application.dto.query.ListPartnersQuery
import com.gijun.wms.master.application.port.`in`.command.ChangePartnerStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.CreatePartnerCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdatePartnerCommandUseCase
import com.gijun.wms.master.application.port.`in`.query.GetPartnerQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.ListPartnersQueryUseCase
import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
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
 * 거래처 마스터 API. gateway 인증 경로라 유효한 JWT 를 가진 사용자만 도달한다.
 * 마스터는 삭제 대신 status 전환(INACTIVE)만 제공한다 — 입출고 문서가 참조하기 때문.
 */
@RestController
@RequestMapping("/api/partners")
class PartnerController(
    private val createPartnerCommandUseCase: CreatePartnerCommandUseCase,
    private val updatePartnerCommandUseCase: UpdatePartnerCommandUseCase,
    private val changePartnerStatusCommandUseCase: ChangePartnerStatusCommandUseCase,
    private val getPartnerQueryUseCase: GetPartnerQueryUseCase,
    private val listPartnersQueryUseCase: ListPartnersQueryUseCase,
) {

    /** 거래처 생성 — 코드 중복 시 409. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreatePartnerRequest): ApiResponse<PartnerResponse> {
        val result = createPartnerCommandUseCase.createPartner(
            CreatePartnerCommand(code = request.code, name = request.name, type = request.type),
        )
        return ApiResponse.ok(PartnerResponse.from(result))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) status: PartnerStatus?,
        @RequestParam(required = false) type: PartnerType?,
    ): ApiResponse<List<PartnerResponse>> {
        val results = listPartnersQueryUseCase.listPartners(ListPartnersQuery(status, type))
        return ApiResponse.ok(results.map(PartnerResponse::from))
    }

    @GetMapping("/{partnerId}")
    fun get(@PathVariable partnerId: Long): ApiResponse<PartnerResponse> {
        val result = getPartnerQueryUseCase.getPartner(GetPartnerQuery(partnerId))
        return ApiResponse.ok(PartnerResponse.from(result))
    }

    /** 수정 — code 는 비즈니스 키라 변경 불가, name/type 만. */
    @PutMapping("/{partnerId}")
    fun update(
        @PathVariable partnerId: Long,
        @Valid @RequestBody request: UpdatePartnerRequest,
    ): ApiResponse<PartnerResponse> {
        val result = updatePartnerCommandUseCase.updatePartner(
            UpdatePartnerCommand(partnerId = partnerId, name = request.name, type = request.type),
        )
        return ApiResponse.ok(PartnerResponse.from(result))
    }

    /** 활성/비활성 전환 — 같은 상태로의 전환은 409. */
    @PatchMapping("/{partnerId}/status")
    fun changeStatus(
        @PathVariable partnerId: Long,
        @Valid @RequestBody request: ChangePartnerStatusRequest,
    ): ApiResponse<PartnerResponse> {
        val result = changePartnerStatusCommandUseCase.changePartnerStatus(
            ChangePartnerStatusCommand(partnerId, request.status),
        )
        return ApiResponse.ok(PartnerResponse.from(result))
    }
}
