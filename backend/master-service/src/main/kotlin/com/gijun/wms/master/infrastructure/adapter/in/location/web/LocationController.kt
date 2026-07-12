package com.gijun.wms.master.infrastructure.adapter.`in`.location.web

import com.gijun.wms.master.application.dto.command.ChangeLocationStatusCommand
import com.gijun.wms.master.application.dto.command.CreateLocationCommand
import com.gijun.wms.master.application.dto.command.UpdateLocationCommand
import com.gijun.wms.master.application.dto.query.GetLocationQuery
import com.gijun.wms.master.application.dto.query.ListLocationsQuery
import com.gijun.wms.master.application.port.`in`.command.ChangeLocationStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.CreateLocationCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdateLocationCommandUseCase
import com.gijun.wms.master.application.port.`in`.query.GetLocationQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.ListLocationsQueryUseCase
import com.gijun.wms.master.domain.enums.LocationStatus
import com.gijun.wms.shared.web.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 로케이션 마스터 API. gateway 인증 경로라 유효한 JWT 를 가진 사용자만 도달한다.
 * 마스터는 삭제 대신 status 전환(INACTIVE)만 제공한다 — 재고원장이 참조하기 때문.
 */
@RestController
@RequestMapping("/api/locations")
class LocationController(
    private val createLocationCommandUseCase: CreateLocationCommandUseCase,
    private val updateLocationCommandUseCase: UpdateLocationCommandUseCase,
    private val changeLocationStatusCommandUseCase: ChangeLocationStatusCommandUseCase,
    private val getLocationQueryUseCase: GetLocationQueryUseCase,
    private val listLocationsQueryUseCase: ListLocationsQueryUseCase,
) {

    /** 로케이션 생성 — 코드 중복 시 409. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateLocationRequest): ApiResponse<LocationResponse> {
        val result = createLocationCommandUseCase.createLocation(
            CreateLocationCommand(code = request.code, zone = request.zone, type = request.type),
        )
        return ApiResponse.ok(LocationResponse.from(result))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) status: LocationStatus?,
        @RequestParam(required = false) zone: String?,
    ): ApiResponse<List<LocationResponse>> {
        val results = listLocationsQueryUseCase.listLocations(ListLocationsQuery(status, zone))
        return ApiResponse.ok(results.map(LocationResponse::from))
    }

    @GetMapping("/{locationId}")
    fun get(@PathVariable locationId: Long): ApiResponse<LocationResponse> {
        val result = getLocationQueryUseCase.getLocation(GetLocationQuery(locationId))
        return ApiResponse.ok(LocationResponse.from(result))
    }

    /** 수정 — code 는 비즈니스 키라 변경 불가, zone/type(용도 변경)만. */
    @PutMapping("/{locationId}")
    fun update(
        @PathVariable locationId: Long,
        @Valid @RequestBody request: UpdateLocationRequest,
    ): ApiResponse<LocationResponse> {
        val result = updateLocationCommandUseCase.updateLocation(
            UpdateLocationCommand(locationId = locationId, zone = request.zone, type = request.type),
        )
        return ApiResponse.ok(LocationResponse.from(result))
    }

    /** 활성/비활성 전환 — 같은 상태로의 전환은 409. */
    @PatchMapping("/{locationId}/status")
    fun changeStatus(
        @PathVariable locationId: Long,
        @Valid @RequestBody request: ChangeLocationStatusRequest,
    ): ApiResponse<LocationResponse> {
        val result = changeLocationStatusCommandUseCase.changeLocationStatus(
            ChangeLocationStatusCommand(locationId, request.status),
        )
        return ApiResponse.ok(LocationResponse.from(result))
    }
}
