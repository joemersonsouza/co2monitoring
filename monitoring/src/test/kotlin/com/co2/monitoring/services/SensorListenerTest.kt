package com.co2.monitoring.services

import com.co2.monitoring.repositories.SensorMeasurementRepository
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Sensor
import com.co2.monitoring.repositories.entities.SensorMeasurement
import com.co2.monitoring.services.dto.SensorMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verifyNoInteractions
import org.springframework.amqp.core.Message
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
class SensorListenerTest {
    @Mock
    private lateinit var sensorService: SensorService

    @Mock
    private lateinit var sensorMeasurementRepository: SensorMeasurementRepository

    @Mock
    private lateinit var alertService: AlertService

    @Mock
    private lateinit var metricsService: MetricsService

    private val mapper = ObjectMapper()
    private lateinit var sensorListener: SensorListener
    private val sensorId = UUID.randomUUID()
    private val sensor = Sensor(
        sensorId,
        SensorStatus.OK,
        LocalDateTime.now()
    )

    @BeforeEach
    fun setUp() {
        sensorListener = SensorListener(sensorService, sensorMeasurementRepository, alertService, metricsService)
    }

    @Test
    fun `Validate empty message`() {
        sensorListener.onMessage(null)

        verifyNoInteractions(sensorService)
    }

    @Test
    fun `Process a sensor message to create a new sensor measurement`() {
        val message = SensorMessage(sensorId, co2 = 1000)
        `when`(sensorService.findById(sensorId)).thenReturn(sensor)
        `when`(sensorMeasurementRepository.findTop3BySensorOrderByCreatedAtDesc(sensor))
            .thenReturn(
                listOf(
                    SensorMeasurement(value = 1000L),
                    SensorMeasurement(value = 1500L),
                    SensorMeasurement(value = 1900L)
                )
            )

        sensorListener.onMessage(Message(mapper.writeValueAsString(message).toByteArray()))
    }
}
