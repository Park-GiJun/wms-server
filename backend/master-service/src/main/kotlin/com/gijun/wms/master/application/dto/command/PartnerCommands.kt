package com.gijun.wms.master.application.dto.command

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType

/** 거래처 생성 — ACTIVE 로 시작한다. */
data class CreatePartnerCommand(
    val code: String,
    val name: String,
    val type: PartnerType,
)

/** 거래처 수정 — code 는 비즈니스 키라 변경 불가. */
data class UpdatePartnerCommand(
    val partnerId: Long,
    val name: String,
    val type: PartnerType,
)

/** 거래처 활성/비활성 전환 — 마스터는 삭제 대신 INACTIVE. */
data class ChangePartnerStatusCommand(
    val partnerId: Long,
    val status: PartnerStatus,
)
