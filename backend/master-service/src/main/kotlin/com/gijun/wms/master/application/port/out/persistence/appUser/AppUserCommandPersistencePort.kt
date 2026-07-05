package com.gijun.wms.master.application.port.out.persistence.appUser

import com.gijun.wms.master.domain.appUser.AppUserModel

interface AppUserCommandPersistencePort {
    fun save(model: AppUserModel): AppUserModel
    fun findById(id: Long): AppUserModel?
}
