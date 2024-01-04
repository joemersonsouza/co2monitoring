package com.co2.monitoring.controllers

import com.co2.monitoring.controllers.dto.MeasurementRequest
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Alert
import com.co2.monitoring.repositories.entities.Metrics
import com.co2.monitoring.services.AlertService
import com.co2.monitoring.services.MetricsService
import com.co2.monitoring.services.SensorService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
class SensorsControllerTest {
    @Mock
    private lateinit var sensorService: SensorService

    @Mock
    private lateinit var metricsService: MetricsService

    @Mock
    private lateinit var alertService: AlertService

    private lateinit var sensorsController: SensorsController
    private val sensorId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        sensorsController = SensorsController(sensorService, metricsService, alertService)
    }

    @Test
    fun `Get the sensor status and returns WARN`() {
        `when`(sensorService.getStatusBySensorId(sensorId)).thenReturn(SensorStatus.WARN)

        val result = sensorsController.getSensorStatus(sensorId)

        assertEquals(SensorStatus.WARN.name, result.status)
    }

    @Test
    fun `Get metrics and return successfully`() {
        val maxMeasurerLast30Days = 200L
        `when`(metricsService.getMetricsBySensorId(sensorId)).thenReturn(Metrics(maxMeasurerLast30Days = maxMeasurerLast30Days))

        val result = sensorsController.getSensorMetrics(sensorId)

        assertEquals(maxMeasurerLast30Days, result.maxLast30Days)
    }

    @Test
    fun `List all alerts created`() {
        val firstMeasurement = 3200L
        val alerts = listOf(Alert(firstMeasurement = firstMeasurement))
        `when`(alertService.getAlertsBySensorId(sensorId)).thenReturn(alerts)

        val result = sensorsController.getSensorAlerts(sensorId)

        assertEquals(firstMeasurement, result.first().measurement1)
    }

    @Test
    fun `Save a sensor and do not thrown any error`() {
        sensorsController.saveSensorMeasurer(
            sensorId,
            MeasurementRequest(0L, LocalDateTime.now())
        )
    }
}
