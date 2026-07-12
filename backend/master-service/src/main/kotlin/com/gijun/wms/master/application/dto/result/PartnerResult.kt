package com.gijun.wms.master.application.dto.result

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import com.gijun.wms.master.domain.partner.PartnerModel

data class PartnerResult(
    val id: Long,
    val code: String,
    val name: String,
    val type: PartnerType,
    val status: PartnerStatus,
) {
    companion object {
        fun from(model: PartnerModel): PartnerResult = PartnerResult(
            id = requireNotNull(model.id) { "저장된 거래처는 id 가 있어야 한다" },
            code = model.code,
            name = model.name,
            type = model.type,
            status = model.status,
        )
    }
}
