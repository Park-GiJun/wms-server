package com.gijun.wms.master.application.handler.command

import com.gijun.wms.master.application.dto.command.ApproveAppUserCommand
import com.gijun.wms.master.application.dto.command.LoginAppUserCommand
import com.gijun.wms.master.application.dto.command.RegisterAppUserCommand
import com.gijun.wms.master.application.dto.command.RejectAppUserCommand
import com.gijun.wms.master.application.dto.result.AppUserResult
import com.gijun.wms.master.application.dto.result.TokenResult
import com.gijun.wms.master.application.port.`in`.command.ApproveAppUserCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.LoginAppUserCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.RegisterAppUserCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.RejectAppUserCommandUseCase
import com.gijun.wms.master.application.port.out.persistence.appUser.AppUserCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.appUser.AppUserQueryPersistencePort
import com.gijun.wms.master.application.port.out.security.PasswordHasherPort
import com.gijun.wms.master.application.port.out.token.TokenIssuerPort
import com.gijun.wms.master.domain.appUser.AppUserModel
import com.gijun.wms.master.domain.appUser.exception.AppUserException
import com.gijun.wms.master.domain.appUser.service.PasswordPolicy
import com.gijun.wms.master.domain.enums.AppUserStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AppUserCommandHandler(
    private val appUserCommandPersistencePort: AppUserCommandPersistencePort,
    private val appUserQueryPersistencePort: AppUserQueryPersistencePort,
    private val passwordHasherPort: PasswordHasherPort,
    private val tokenIssuerPort: TokenIssuerPort,
) : RegisterAppUserCommandUseCase, ApproveAppUserCommandUseCase, RejectAppUserCommandUseCase,
    LoginAppUserCommandUseCase {

    /** login — 비밀번호 검증 후 상태 확인(계정 존재 여부를 숨기려 자격 검증이 먼저), ACTIVE 만 발급. */
    @Transactional(readOnly = true)
    override fun login(command: LoginAppUserCommand): TokenResult {
        val user = appUserQueryPersistencePort.findByEmail(command.email)
            ?: throw AppUserException.InvalidCredentialsException()
        if (!passwordHasherPort.matches(command.password, user.passwordHash)) {
            throw AppUserException.InvalidCredentialsException()
        }
        if (user.status != AppUserStatus.ACTIVE) {
            throw AppUserException.NotActiveException(user.status)
        }
        return tokenIssuerPort.issue(
            userId = requireNotNull(user.id),
            email = user.email,
            role = user.userRole,
        )
    }

    /** register — 비밀번호 정책·이메일 중복 검증 후 PENDING 으로 저장. */
    @Transactional
    override fun register(command: RegisterAppUserCommand): AppUserResult {
        PasswordPolicy.validate(command.password)
        if (appUserQueryPersistencePort.existsByEmail(command.email)) {
            throw AppUserException.DuplicateUserException(command.email)
        }
        val saved = appUserCommandPersistencePort.save(
            AppUserModel.register(command.email, passwordHasherPort.hash(command.password)),
        )
        return AppUserResult.from(saved)
    }

    /** admin confirm — PENDING → ACTIVE. 상태 전이 검증은 도메인(approve)이 한다. */
    @Transactional
    override fun approve(command: ApproveAppUserCommand): AppUserResult {
        val user = loadUser(command.userId)
        return AppUserResult.from(appUserCommandPersistencePort.save(user.approve(command.adminId)))
    }

    /** admin reject — PENDING → REJECTED. */
    @Transactional
    override fun reject(command: RejectAppUserCommand): AppUserResult {
        val user = loadUser(command.userId)
        return AppUserResult.from(appUserCommandPersistencePort.save(user.reject(command.adminId)))
    }

    private fun loadUser(userId: Long): AppUserModel =
        appUserCommandPersistencePort.findById(userId)
            ?: throw AppUserException.UserNotFoundException(userId)
}
