package com.co2.monitoring.services

import com.co2.monitoring.repositories.AlertRepository
import com.co2.monitoring.repositories.entities.Alert
import com.co2.monitoring.repositories.entities.Sensor
import com.co2.monitoring.repositories.entities.SensorMeasurement
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AlertService(
    private val alertRepository: AlertRepository,
    private val sensorService: SensorService
) {
    fun getAlertsBySensorId(sensorId: UUID): List<Alert> {
        val sensor = sensorService.findById(sensorId)
        return alertRepository.findAllBySensor(sensor)
    }

    fun saveAlertBasedOnSensorData(sensor: Sensor, sensorMeasurements: List<SensorMeasurement>) {
        closeAlertBySensorId(sensor)
        val alert = Alert(
            UUID.randomUUID(),
            sensor,
            LocalDateTime.now(),
            null,
            firstMeasurement = sensorMeasurements.getOrNull(2)?.value ?: 0,
            secondMeasurement = sensorMeasurements.getOrNull(1)?.value ?: 0,
            lastMeasurement = sensorMeasurements.getOrNull(0)?.value ?: 0
        )

        alertRepository.save(alert)
    }

    fun closeAlertBySensorId(sensor: Sensor) {
        val alert = alertRepository.findBySensorAndEndTimeIsNull(sensor)
        if(alert.isPresent) {
            alertRepository.save(alert.get().copy(endTime = LocalDateTime.now()))
        }
    }
}
