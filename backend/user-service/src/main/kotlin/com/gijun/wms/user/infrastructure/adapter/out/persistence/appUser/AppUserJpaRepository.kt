package com.gijun.wms.user.infrastructure.adapter.out.persistence.appUser

import org.springframework.data.jpa.repository.JpaRepository

interface AppUserJpaRepository : JpaRepository<AppUserJpaEntity, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): AppUserJpaEntity?
}
