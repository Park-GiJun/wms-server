package com.gijun.wms.master.domain.appUser.service

import com.gijun.wms.master.domain.appUser.exception.AppUserException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PasswordPolicyTest {

    @Test
    fun `정책을 만족하는 비밀번호는 통과한다`() {
        PasswordPolicy.validate("abcd1234")
    }

    @Test
    fun `8자 미만이면 거부한다`() {
        assertFailsWith<AppUserException.WeakPasswordException> {
            PasswordPolicy.validate("ab1")
        }
    }

    @Test
    fun `72자 초과면 거부한다`() {
        assertFailsWith<AppUserException.WeakPasswordException> {
            PasswordPolicy.validate("a1" + "x".repeat(71))
        }
    }

    @Test
    fun `영문자가 없으면 거부한다`() {
        assertFailsWith<AppUserException.WeakPasswordException> {
            PasswordPolicy.validate("12345678")
        }
    }

    @Test
    fun `숫자가 없으면 거부한다`() {
        assertFailsWith<AppUserException.WeakPasswordException> {
            PasswordPolicy.validate("abcdefgh")
        }
    }

    @Test
    fun `공백이 있으면 거부한다`() {
        assertFailsWith<AppUserException.WeakPasswordException> {
            PasswordPolicy.validate("abcd 1234")
        }
    }
}
