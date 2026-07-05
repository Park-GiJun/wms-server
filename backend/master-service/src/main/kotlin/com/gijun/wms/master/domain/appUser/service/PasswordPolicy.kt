package com.gijun.wms.master.domain.appUser.service

import com.gijun.wms.master.domain.appUser.exception.AppUserException

/**
 * 비밀번호 업무 규칙 — 순수 도메인 서비스(상태·의존성 없음).
 * web 레이어의 Bean Validation(@Size)은 1차 방어일 뿐, 정책의 원본은 여기다.
 */
object PasswordPolicy {

    private const val MIN_LENGTH = 8
    private const val MAX_LENGTH = 72   // BCrypt 입력 상한

    fun validate(rawPassword: String) {
        if (rawPassword.length < MIN_LENGTH) fail("${MIN_LENGTH}자 이상이어야 합니다")
        if (rawPassword.length > MAX_LENGTH) fail("${MAX_LENGTH}자 이하여야 합니다")
        if (!rawPassword.any { it.isLetter() }) fail("영문자를 포함해야 합니다")
        if (!rawPassword.any { it.isDigit() }) fail("숫자를 포함해야 합니다")
        if (rawPassword.any { it.isWhitespace() }) fail("공백을 포함할 수 없습니다")
    }

    private fun fail(reason: String): Nothing =
        throw AppUserException.WeakPasswordException(reason)
}
