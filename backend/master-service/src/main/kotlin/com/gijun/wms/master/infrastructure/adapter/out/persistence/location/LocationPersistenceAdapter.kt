package com.gijun.wms.master.infrastructure.adapter.out.persistence.location

import com.gijun.wms.master.application.port.out.persistence.location.LocationCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.location.LocationQueryPersistencePort
import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.location.LocationModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

/**
 * 로케이션 퍼시스턴스 어댑터 — command/query 포트 구현. 엔티티↔모델 변환 외 로직 없음.
 */
@Repository
class LocationPersistenceAdapter(
    private val locationJpaRepository: LocationJpaRepository,
) : LocationCommandPersistencePort, LocationQueryPersistencePort {

    override fun save(model: LocationModel): LocationModel =
        locationJpaRepository.save(LocationJpaEntity.from(model)).toModel()

    override fun findById(id: Long): LocationModel? =
        locationJpaRepository.findByIdOrNull(id)?.toModel()

    override fun existsByCode(code: String): Boolean =
        locationJpaRepository.existsByCode(code)

    override fun findLocations(status: LocationStatus?, zone: String?): List<LocationModel> {
        val entities = when {
            status != null && zone != null -> locationJpaRepository.findByStatusAndZone(status, zone)
            status != null -> locationJpaRepository.findByStatus(status)
            zone != null -> locationJpaRepository.findByZone(zone)
            else -> locationJpaRepository.findAll()
        }
        return entities.map { it.toModel() }
    }
}
