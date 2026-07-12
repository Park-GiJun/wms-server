package com.gijun.wms.master.domain.partner

import com.gijun.wms.master.domain.enums.PartnerStatus
import com.gijun.wms.master.domain.enums.PartnerType
import com.gijun.wms.master.domain.partner.exception.PartnerException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PartnerModelTest {

    @Test
    fun `거래처는 ACTIVE 로 생성된다`() {
        val partner = PartnerModel.create("SUP-001", "한빛상사", PartnerType.SUPPLIER)
        assertEquals(PartnerStatus.ACTIVE, partner.status)
    }

    @Test
    fun `거래처 상태 전환 - ACTIVE 에서 INACTIVE 로`() {
        val partner = PartnerModel.create("SUP-001", "한빛상사", PartnerType.SUPPLIER)
        assertEquals(PartnerStatus.INACTIVE, partner.changeStatus(PartnerStatus.INACTIVE).status)
    }

    @Test
    fun `거래처를 같은 상태로 전환하면 예외`() {
        val partner = PartnerModel.create("SUP-001", "한빛상사", PartnerType.SUPPLIER)
        assertFailsWith<PartnerException.AlreadyInStatusException> {
            partner.changeStatus(PartnerStatus.ACTIVE)
        }
    }

    @Test
    fun `거래처 수정은 name-type 만 바꾼다`() {
        val partner = PartnerModel.create("SUP-001", "한빛상사", PartnerType.SUPPLIER)
        val updated = partner.update("한빛무역", PartnerType.BOTH)
        assertEquals("SUP-001", updated.code)
        assertEquals("한빛무역", updated.name)
        assertEquals(PartnerType.BOTH, updated.type)
    }
}
