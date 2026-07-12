package com.gijun.wms.master.infrastructure.adapter.out.persistence.partner

import com.gijun.wms.master.application.port.out.persistence.partner.PartnerCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.partner.PartnerQueryPersistencePort
import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import com.gijun.wms.master.domain.partner.PartnerModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

/**
 * 거래처 퍼시스턴스 어댑터 — command/query 포트 구현. 엔티티↔모델 변환 외 로직 없음.
 */
@Repository
class PartnerPersistenceAdapter(
    private val partnerJpaRepository: PartnerJpaRepository,
) : PartnerCommandPersistencePort, PartnerQueryPersistencePort {

    override fun save(model: PartnerModel): PartnerModel =
        partnerJpaRepository.save(PartnerJpaEntity.from(model)).toModel()

    override fun findById(id: Long): PartnerModel? =
        partnerJpaRepository.findByIdOrNull(id)?.toModel()

    override fun existsByCode(code: String): Boolean =
        partnerJpaRepository.existsByCode(code)

    override fun findPartners(status: PartnerStatus?, type: PartnerType?): List<PartnerModel> {
        val entities = when {
            status != null && type != null -> partnerJpaRepository.findByStatusAndType(status, type)
            status != null -> partnerJpaRepository.findByStatus(status)
            type != null -> partnerJpaRepository.findByType(type)
            else -> partnerJpaRepository.findAll()
        }
        return entities.map { it.toModel() }
    }
}
