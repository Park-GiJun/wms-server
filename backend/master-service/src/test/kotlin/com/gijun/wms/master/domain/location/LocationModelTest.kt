package com.gijun.wms.master.domain.location

import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.master.domain.enums.LocationType
import com.gijun.wms.master.domain.location.exception.LocationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocationModelTest {

    @Test
    fun `로케이션은 ACTIVE 로 생성된다`() {
        val location = LocationModel.create("A-01-02", "A", LocationType.STORAGE)
        assertEquals(LocationStatus.ACTIVE, location.status)
    }

    @Test
    fun `로케이션 상태 전환 - ACTIVE 에서 INACTIVE 로`() {
        val location = LocationModel.create("A-01-02", "A", LocationType.STORAGE)
        assertEquals(LocationStatus.INACTIVE, location.changeStatus(LocationStatus.INACTIVE).status)
    }

    @Test
    fun `로케이션을 같은 상태로 전환하면 예외`() {
        val location = LocationModel.create("A-01-02", "A", LocationType.STORAGE)
        assertFailsWith<LocationException.AlreadyInStatusException> {
            location.changeStatus(LocationStatus.ACTIVE)
        }
    }

    @Test
    fun `로케이션 수정은 zone-type 만 바꾼다`() {
        val location = LocationModel.create("A-01-02", "A", LocationType.STORAGE)
        val updated = location.update("B", LocationType.PICKING)
        assertEquals("A-01-02", updated.code)
        assertEquals("B", updated.zone)
        assertEquals(LocationType.PICKING, updated.type)
    }
}
