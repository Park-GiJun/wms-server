package com.gijun.wms.user.application.handler.query

import com.gijun.wms.user.application.dto.query.VerifyAppUserQuery
import com.gijun.wms.user.application.port.`in`.query.VerifyAppUserQueryUseCase
import com.gijun.wms.user.application.port.out.persistence.appUser.AppUserQueryPersistencePort
import com.gijun.wms.user.domain.appUser.exception.AppUserException
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
