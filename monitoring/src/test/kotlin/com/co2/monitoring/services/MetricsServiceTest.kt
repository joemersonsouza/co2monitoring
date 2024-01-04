package com.co2.monitoring.services;

import com.co2.monitoring.repositories.MetricsRepository
import com.co2.monitoring.repositories.SensorMeasurementRepository
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Metrics
import com.co2.monitoring.repositories.entities.Sensor
import com.co2.monitoring.repositories.entities.SensorMeasurement
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class MetricsServiceTest {
    @Mock
    private lateinit var metricsRepository: MetricsRepository

    @Mock
    private lateinit var sensorMeasurementRepository: SensorMeasurementRepository
    private lateinit var metricsService: MetricsService

    @BeforeEach
    fun setUp() {
        metricsService = MetricsService(metricsRepository, sensorMeasurementRepository)
    }

    private val sensorId = UUID.randomUUID()
    private val sensor = Sensor(
        sensorId,
        SensorStatus.OK,
        LocalDateTime.now()
    )
    private val metrics = Metrics(
        UUID.randomUUID(),
        sensor, 2500L, 1333.33
    )

    @Test
    fun `Get metrics by sensor id`() {
        `when`(metricsRepository.findBySensorUuid(sensorId)).thenReturn(Optional.of(metrics))

        val result = metricsService.getMetricsBySensorId(sensorId)

        assertEquals(metrics.avgMeasurerLast30Days, result.avgMeasurerLast30Days)
    }

    @Test
    fun `Get metrics by sensor`() {
        `when`(metricsRepository.findBySensor(sensor)).thenReturn(Optional.of(metrics))

        val result = metricsService.getMetricsBySensor(sensor).get()

        assertEquals(metrics.avgMeasurerLast30Days, result.avgMeasurerLast30Days)
    }

    @Test
    fun `Save metrics`() {
        metricsService.saveMetrics(sensor, listOf(2100, 2100, 2500))
    }

    @Test
    fun `Update metrics values with a new maximum value`() {
        `when`(sensorMeasurementRepository.findAllBySensorAndCreatedAtAfter(any(), any()))
            .thenReturn(listOf(SensorMeasurement(value = 2100)))
        metricsService.updateMetrics(metrics, 60000L)
    }
}
