package com.gijun.wms.user.application.port.out.persistence.appUser

import com.gijun.wms.user.domain.appUser.AppUserModel

interface AppUserQueryPersistencePort {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): AppUserModel?
}
