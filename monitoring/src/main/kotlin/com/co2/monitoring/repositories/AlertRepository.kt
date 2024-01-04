package com.co2.monitoring.repositories

import com.co2.monitoring.repositories.entities.Alert
import com.co2.monitoring.repositories.entities.Sensor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface AlertRepository: CrudRepository<Alert, UUID> {
    fun findAllBySensor(sensor: Sensor): List<Alert>
    fun findBySensorAndEndTimeIsNull(sensor: Sensor): Optional<Alert>
}
