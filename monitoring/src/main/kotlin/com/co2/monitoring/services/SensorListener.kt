package com.co2.monitoring.services

import com.co2.monitoring.repositories.SensorMeasurementRepository
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Sensor
import com.co2.monitoring.repositories.entities.SensorMeasurement
import com.co2.monitoring.services.dto.SensorMessage
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.stereotype.Service

@Service
class SensorListener(
    private val sensorService: SensorService,
    private val sensorMeasurementRepository: SensorMeasurementRepository,
    private val alertService: AlertService,
    private val metricsService: MetricsService,
) : MessageListener {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = ObjectMapper()
    private val limit = 2000

    @Transactional
    override fun onMessage(message: Message?) {
        val body = message?.body?.let { String(it) }
        if (body.isNullOrBlank()) return

        logger.info("Message Received $body")

        val sensorMessage = mapper.readValue(body, SensorMessage::class.java)
        try {
            handleSensorMessage(sensorMessage)
        } catch (exception: Exception) {
            logger.error("The message $message was not processed. Error: ${exception.message}")
        }
    }

    private fun handleSensorMessage(message: SensorMessage) {
        val sensor = sensorService.findById(message.sensorId)

        sensorMeasurementRepository.save(
            SensorMeasurement(
                value = message.co2,
                sensor = sensor,
                createdAt = message.createdAt.toLocalDateTime()
            )
        )

        val measurements = sensorMeasurementRepository.findTop3BySensorOrderByCreatedAtDesc(sensor)

        val status = handleSensorStatus(measurements)

        handleAlert(status, sensor, measurements)

        handleMetrics(sensor, measurements, message)

        sensorService.save(sensor.copy(status = status))
    }

    private fun handleMetrics(
        sensor: Sensor,
        measurements: List<SensorMeasurement>,
        message: SensorMessage
    ) {
        val metrics = metricsService.getMetricsBySensor(sensor)
        val measurementValues = measurements.map { it.value }
        if (!metrics.isPresent) {
            metricsService.saveMetrics(sensor, measurementValues)
        } else {
            val maximumValue = if (metrics.get().maxMeasurerLast30Days < message.co2) message.co2 else metrics.get().maxMeasurerLast30Days
            metricsService.updateMetrics(metrics.get(), maximumValue)
        }
    }

    private fun handleAlert(
        status: SensorStatus,
        sensor: Sensor,
        sensorMeasurements: List<SensorMeasurement>
    ) {
        if (status == SensorStatus.ALERT) {
            alertService.saveAlertBasedOnSensorData(sensor, sensorMeasurements)
        } else if (status == SensorStatus.OK) {
            alertService.closeAlertBySensorId(sensor)
        }
    }

    private fun handleSensorStatus(
        sensorMeasurements: List<SensorMeasurement>
    ): SensorStatus {

        if (sensorMeasurements.size < 2)
            return SensorStatus.OK

        val measurements = sensorMeasurements.map { it.value }

        if (measurements[0] > limit && measurements[1] > limit && measurements[2] > limit)
            return SensorStatus.ALERT

        if (measurements.first() > limit)
            return SensorStatus.WARN

        return SensorStatus.OK
    }
}
