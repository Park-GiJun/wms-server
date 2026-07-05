package com.gijun.wms.master.infrastructure.adapter.out.persistence.appUser

import com.gijun.wms.master.application.port.out.persistence.appUser.AppUserCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.appUser.AppUserQueryPersistencePort
import com.gijun.wms.master.domain.appUser.AppUserModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

/**
 * appUser 퍼시스턴스 어댑터 — command/query 포트 구현. 엔티티↔모델 변환 외 로직 없음.
 */
@Repository
class AppUserPersistenceAdapter(
    private val appUserJpaRepository: AppUserJpaRepository,
) : AppUserCommandPersistencePort, AppUserQueryPersistencePort {

    override fun save(model: AppUserModel): AppUserModel =
        appUserJpaRepository.save(AppUserJpaEntity.from(model)).toModel()

    override fun findById(id: Long): AppUserModel? =
        appUserJpaRepository.findByIdOrNull(id)?.toModel()

    override fun existsByEmail(email: String): Boolean =
        appUserJpaRepository.existsByEmail(email)

    override fun findByEmail(email: String): AppUserModel? =
        appUserJpaRepository.findByEmail(email)?.toModel()
}
