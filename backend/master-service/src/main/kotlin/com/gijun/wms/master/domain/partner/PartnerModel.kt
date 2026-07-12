package com.gijun.wms.master.domain.partner

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import com.gijun.wms.master.domain.partner.exception.PartnerException
import java.time.Instant

/**
 * 거래처 — 입고(SUPPLIER)/출고(CUSTOMER) 문서가 참조하는 마스터.
 * 지금은 최소 골격(코드/이름/유형)만 — 연락처·주소 등은 입출고 피처가 필요로 할 때 확장한다.
 */
data class PartnerModel(
    val id: Long?,
    val code: String,
    val name: String,
    val type: PartnerType,
    val status: PartnerStatus,
    val createdAt: Instant?,
    val modifiedAt: Instant?,
) {
    /** code 는 비즈니스 키라 변경 불가. */
    fun update(name: String, type: PartnerType): PartnerModel = copy(
        name = name,
        type = type,
        modifiedAt = Instant.now(),
    )

    /** ACTIVE ↔ INACTIVE 전환. 같은 상태로의 전환은 거부한다(중복 요청 감지). */
    fun changeStatus(target: PartnerStatus): PartnerModel {
        if (status == target) {
            throw PartnerException.AlreadyInStatusException(target)
        }
        return copy(status = target, modifiedAt = Instant.now())
    }

    companion object {
        fun create(code: String, name: String, type: PartnerType): PartnerModel = PartnerModel(
            id = null,
            code = code,
            name = name,
            type = type,
            status = PartnerStatus.ACTIVE,
            createdAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
    }
}
