package com.gijun.wms.master.application.handler.command

import com.gijun.wms.master.application.dto.command.ChangeLocationStatusCommand
import com.gijun.wms.master.application.dto.command.CreateLocationCommand
import com.gijun.wms.master.application.dto.command.UpdateLocationCommand
import com.gijun.wms.master.application.dto.result.LocationResult
import com.gijun.wms.master.application.port.`in`.command.ChangeLocationStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.CreateLocationCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdateLocationCommandUseCase
import com.gijun.wms.master.application.port.out.persistence.location.LocationCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.location.LocationQueryPersistencePort
import com.gijun.wms.master.domain.location.LocationModel
import com.gijun.wms.master.domain.location.exception.LocationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationCommandHandler(
    private val locationCommandPersistencePort: LocationCommandPersistencePort,
    private val locationQueryPersistencePort: LocationQueryPersistencePort,
) : CreateLocationCommandUseCase, UpdateLocationCommandUseCase, ChangeLocationStatusCommandUseCase {

    @Transactional
    override fun createLocation(command: CreateLocationCommand): LocationResult {
        if (locationQueryPersistencePort.existsByCode(command.code)) {
            throw LocationException.DuplicateLocationCodeException(command.code)
        }
        return LocationResult.from(
            locationCommandPersistencePort.save(
                LocationModel.create(code = command.code, zone = command.zone, type = command.type),
            ),
        )
    }

    @Transactional
    override fun updateLocation(command: UpdateLocationCommand): LocationResult {
        val location = loadLocation(command.locationId)
        return LocationResult.from(
            locationCommandPersistencePort.save(
                location.update(zone = command.zone, type = command.type),
            ),
        )
    }

    /** 상태 전이 검증(같은 상태 거부)은 도메인(changeStatus)이 한다. */
    @Transactional
    override fun changeLocationStatus(command: ChangeLocationStatusCommand): LocationResult {
        val location = loadLocation(command.locationId)
        return LocationResult.from(
            locationCommandPersistencePort.save(location.changeStatus(command.status)),
        )
    }

    private fun loadLocation(locationId: Long): LocationModel =
        locationCommandPersistencePort.findById(locationId)
            ?: throw LocationException.LocationNotFoundException(locationId)
}
