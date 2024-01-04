package com.co2.monitoring.services

import com.co2.monitoring.repositories.MetricsRepository
import com.co2.monitoring.repositories.SensorMeasurementRepository
import com.co2.monitoring.repositories.entities.Metrics
import com.co2.monitoring.repositories.entities.Sensor
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class MetricsService(
    private val metricsRepository: MetricsRepository,
    private val sensorMeasurementRepository: SensorMeasurementRepository
) {

    fun getMetricsBySensorId(sensorId: UUID): Metrics {
        return metricsRepository.findBySensorUuid(sensorId)
            .orElse(Metrics())
    }

    fun getMetricsBySensor(sensor: Sensor): Optional<Metrics> {
        return metricsRepository.findBySensor(sensor)
    }

    fun saveMetrics(sensor: Sensor, sensorMeasurement: List<Long>) {
        val metrics = Metrics(
            sensor = sensor,
            avgMeasurerLast30Days = sensorMeasurement.average(),
            maxMeasurerLast30Days = sensorMeasurement.max()
        )

        metricsRepository.save(metrics)
    }

    fun updateMetrics(metrics: Metrics, maximumValue: Long) {
        val daysAgo = LocalDateTime.now().minusDays(30)
        val last30DaysMeasurements =
            sensorMeasurementRepository.findAllBySensorAndCreatedAtAfter(metrics.sensor, daysAgo)
        val average = last30DaysMeasurements.map { it.value }.average()

        metricsRepository.save(
            metrics.copy(maxMeasurerLast30Days = maximumValue, avgMeasurerLast30Days = average)
        )
    }
}
