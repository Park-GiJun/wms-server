package com.gijun.wms.master.application.dto.result

import com.gijun.wms.master.domain.appUser.AppUserModel
import com.gijun.wms.master.domain.enums.AppUserStatus
import com.gijun.wms.master.domain.enums.UserRoles

/** 유스케이스 반환용 — passwordHash 는 절대 밖으로 내보내지 않는다. */
data class AppUserResult(
    val id: Long,
    val email: String,
    val status: AppUserStatus,
    val userRole: UserRoles,
) {
    companion object {
        fun from(model: AppUserModel): AppUserResult = AppUserResult(
            id = requireNotNull(model.id) { "저장된 사용자는 id 가 있어야 한다" },
            email = model.email,
            status = model.status,
            userRole = model.userRole,
        )
    }
}
