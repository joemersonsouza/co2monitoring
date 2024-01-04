package com.co2.monitoring.services;

import com.co2.monitoring.exceptions.SensorNotFoundException
import com.co2.monitoring.repositories.SensorRepository
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Sensor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class SensorServiceTest {
    @Mock
    private lateinit var sensorRepository: SensorRepository

    @Mock
    private lateinit var rabbitTemplate: RabbitTemplate

    private lateinit var sensorService: SensorService

    private val sensorId = UUID.randomUUID()
    private val sensor = Sensor(UUID.randomUUID(), SensorStatus.OK, LocalDateTime.now())

    @BeforeEach
    fun setUp() {
        sensorService = SensorService(sensorRepository, rabbitTemplate)
    }

    @Test
    fun `Find and return a sensor by id`() {
        `when`(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor))

        val result = sensorService.findById(sensorId)

        assertEquals(sensor.uuid, result.uuid)
    }

    @Test
    fun `Get the sensor status by id`() {
        `when`(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor))

        val result = sensorService.getStatusBySensorId(sensorId)

        assertEquals(SensorStatus.OK, result);
    }

    @Test
    fun `Send sensor to the queue but not found`() {
        assertThrows<SensorNotFoundException> {
            sensorService.save(sensorId, 1300L, LocalDateTime.now())
        }
    }

    @Test
    fun `Send sensor to the queue`() {
        `when`(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor))
        sensorService.save(sensorId, 1300L, LocalDateTime.now())
    }

    @Test
    fun `Update sensor with new values`() {
        sensorService.save(sensor)
    }
}
