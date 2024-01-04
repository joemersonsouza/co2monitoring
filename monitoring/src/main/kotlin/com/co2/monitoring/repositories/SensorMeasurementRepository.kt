package com.co2.monitoring.repositories

import com.co2.monitoring.repositories.entities.Sensor
import com.co2.monitoring.repositories.entities.SensorMeasurement
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime
import java.util.UUID

interface SensorMeasurementRepository: CrudRepository<SensorMeasurement, UUID> {
    fun findTop3BySensorOrderByCreatedAtDesc(sensor: Sensor): List<SensorMeasurement>
    fun findAllBySensorAndCreatedAtAfter(sensor: Sensor, from: LocalDateTime): List<SensorMeasurement>
}
