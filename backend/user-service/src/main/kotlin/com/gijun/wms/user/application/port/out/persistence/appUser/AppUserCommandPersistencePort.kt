package com.gijun.wms.user.application.port.out.persistence.appUser

import com.gijun.wms.user.domain.appUser.AppUserModel

interface AppUserCommandPersistencePort {
    fun save(model: AppUserModel): AppUserModel
    fun findById(id: Long): AppUserModel?
}
