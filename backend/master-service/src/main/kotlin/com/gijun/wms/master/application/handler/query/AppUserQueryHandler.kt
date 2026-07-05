package com.gijun.wms.master.application.handler.query

import com.gijun.wms.master.application.dto.query.VerifyAppUserQuery
import com.gijun.wms.master.application.port.`in`.query.VerifyAppUserQueryUseCase
import com.gijun.wms.master.application.port.out.persistence.appUser.AppUserQueryPersistencePort
import com.gijun.wms.master.domain.appUser.exception.AppUserException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AppUserQueryHandler(
    private val appUserQueryPersistencePort: AppUserQueryPersistencePort,
) : VerifyAppUserQueryUseCase {

    @Transactional(readOnly = true)
    override fun verifyAppUser(query: VerifyAppUserQuery) {
        if (appUserQueryPersistencePort.existsByEmail(query.email)) {
            throw AppUserException.DuplicateUserException(query.email)
        }
    }
}
