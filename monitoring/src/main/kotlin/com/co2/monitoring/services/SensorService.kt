package com.co2.monitoring.services

import com.co2.monitoring.configurations.exchange
import com.co2.monitoring.configurations.queue
import com.co2.monitoring.exceptions.SensorNotFoundException
import com.co2.monitoring.repositories.SensorRepository
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Sensor
import com.co2.monitoring.services.dto.SensorMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Service
class SensorService(
    private val sensorRepository: SensorRepository,
    private val rabbitTemplate: RabbitTemplate
) {
    private val mapper = ObjectMapper()

    fun findById(sensorId: UUID): Sensor {
        return sensorRepository.findById(sensorId)
            .orElseThrow { throw SensorNotFoundException() }
    }

    fun getStatusBySensorId(sensorId: UUID): SensorStatus {
        return findById(sensorId).status
    }

    fun save(sensorId: UUID, co2: Long, createdAt: LocalDateTime) {
        findById(sensorId)
        val body = mapper.writeValueAsString(SensorMessage(sensorId, co2, Timestamp.valueOf(createdAt)))
        rabbitTemplate.convertAndSend(exchange, queue, body)
    }

    fun save(sensor: Sensor) {
        sensorRepository.save(sensor)
    }
}
