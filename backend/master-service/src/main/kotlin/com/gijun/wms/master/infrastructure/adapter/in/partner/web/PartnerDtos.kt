package com.gijun.wms.master.infrastructure.adapter.`in`.partner.web

import com.gijun.wms.master.application.dto.result.PartnerResult
import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreatePartnerRequest(
    @field:NotBlank @field:Size(max = 50)
    val code: String,
    @field:NotBlank @field:Size(max = 255)
    val name: String,
    val type: PartnerType,
)

data class UpdatePartnerRequest(
    @field:NotBlank @field:Size(max = 255)
    val name: String,
    val type: PartnerType,
)

/** 활성/비활성 전환 요청. */
data class ChangePartnerStatusRequest(
    val status: PartnerStatus,
)

data class PartnerResponse(
    val id: Long,
    val code: String,
    val name: String,
    val type: PartnerType,
    val status: PartnerStatus,
) {
    companion object {
        fun from(result: PartnerResult): PartnerResponse = PartnerResponse(
            id = result.id,
            code = result.code,
            name = result.name,
            type = result.type,
            status = result.status,
        )
    }
}
