package com.gijun.wms.master.application.handler.query

import com.gijun.wms.master.application.dto.query.GetLocationQuery
import com.gijun.wms.master.application.dto.query.ListLocationsQuery
import com.gijun.wms.master.application.dto.result.LocationResult
import com.gijun.wms.master.application.port.`in`.query.GetLocationQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.ListLocationsQueryUseCase
import com.gijun.wms.master.application.port.out.persistence.location.LocationQueryPersistencePort
import com.gijun.wms.master.domain.location.exception.LocationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationQueryHandler(
    private val locationQueryPersistencePort: LocationQueryPersistencePort,
) : GetLocationQueryUseCase, ListLocationsQueryUseCase {

    @Transactional(readOnly = true)
    override fun getLocation(query: GetLocationQuery): LocationResult =
        LocationResult.from(
            locationQueryPersistencePort.findById(query.locationId)
                ?: throw LocationException.LocationNotFoundException(query.locationId),
        )

    @Transactional(readOnly = true)
    override fun listLocations(query: ListLocationsQuery): List<LocationResult> =
        locationQueryPersistencePort.findLocations(query.status, query.zone)
            .map(LocationResult::from)
}
