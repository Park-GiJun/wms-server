package com.gijun.wms.master.application.port.out.persistence.appUser

import com.gijun.wms.master.domain.appUser.AppUserModel

interface AppUserQueryPersistencePort {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): AppUserModel?
}
