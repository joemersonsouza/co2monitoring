package com.co2.monitoring.services

import com.co2.monitoring.repositories.AlertRepository
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Alert
import com.co2.monitoring.repositories.entities.Sensor
import com.co2.monitoring.repositories.entities.SensorMeasurement
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class AlertServiceTest {
    @Mock
    private lateinit var alertRepository: AlertRepository

    @Mock
    private lateinit var sensorService: SensorService
    private lateinit var alertService: AlertService
    private val sensorId = UUID.randomUUID()
    private val sensor = Sensor(UUID.randomUUID(), SensorStatus.ALERT, LocalDateTime.now())

    @BeforeEach
    fun setUp() {
        alertService = AlertService(alertRepository, sensorService)
    }

    @Test
    fun `Get alert by a sensor`() {
        val alert = Alert(
            UUID.randomUUID(),
            sensor,
            LocalDateTime.now(),
            LocalDateTime.now(),
            2100L,
            2111L,
            2000L
        )
        `when`(sensorService.findById(sensorId)).thenReturn(sensor)
        `when`(alertRepository.findAllBySensor(sensor)).thenReturn(listOf(alert))

        val result = alertService.getAlertsBySensorId(sensorId)

        assertEquals(result[0].uuid, alert.uuid)
    }

    @Test
    fun `Save alert based on sensor measurements`() {
        `when`(alertRepository.findBySensorAndEndTimeIsNull(sensor))
            .thenReturn(Optional.of(Alert()))

        alertService.saveAlertBasedOnSensorData(
            sensor,
            listOf(
                SensorMeasurement(
                    UUID.randomUUID(),
                    LocalDateTime.now(),
                    2500L,
                    sensor
                )
            )
        )
    }

    @Test
    fun `Close alert when a new alert is created`() {
        `when`(alertRepository.findBySensorAndEndTimeIsNull(sensor))
            .thenReturn(Optional.of(Alert()))

        alertService.closeAlertBySensorId(sensor)
    }
}
