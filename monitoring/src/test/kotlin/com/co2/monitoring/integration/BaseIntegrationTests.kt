package com.co2.monitoring.integration

import com.co2.monitoring.repositories.AlertRepository
import com.co2.monitoring.repositories.MetricsRepository
import com.co2.monitoring.repositories.SensorRepository
import com.co2.monitoring.repositories.SensorStatus
import com.co2.monitoring.repositories.entities.Alert
import com.co2.monitoring.repositories.entities.Metrics
import com.co2.monitoring.repositories.entities.Sensor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import java.time.LocalDateTime
import java.util.*


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
abstract class BaseIntegrationTests {

    @Autowired
    protected lateinit var sensorRepository: SensorRepository

    @Autowired
    protected lateinit var metricsRepository: MetricsRepository

    @Autowired
    protected lateinit var alertRepository: AlertRepository

    @Autowired
    protected lateinit var mockMvc: MockMvc

    protected val mapper = ObjectMapper()
    protected val sensor = Sensor(UUID.randomUUID(), SensorStatus.OK, LocalDateTime.now().minusDays(1))

    protected fun saveSensor() {
        sensorRepository.save(sensor)
    }

    protected fun saveMetrics(): Metrics {
        return metricsRepository.save(
            Metrics(
                sensor = sensor,
                maxMeasurerLast30Days = 1200L,
                avgMeasurerLast30Days = 1200.00
            )
        )
    }

    protected fun saveAlerts() {
        alertRepository.save(
            Alert(sensor=sensor, firstMeasurement = 2100L, secondMeasurement = 2200L, lastMeasurement = 4000L)
        )
    }

    companion object {

        @JvmStatic
        @Container
        var rabbitContainer: RabbitMQContainer = RabbitMQContainer("rabbitmq:3-management")

        @JvmStatic
        @DynamicPropertySource
        fun configure(registry: DynamicPropertyRegistry) {
            registry.add("spring.rabbitmq.host") { rabbitContainer.host }
            registry.add("spring.rabbitmq.port") { rabbitContainer.amqpPort }
            registry.add("spring.rabbitmq.username") { rabbitContainer.adminUsername }
            registry.add("spring.rabbitmq.password") { rabbitContainer.adminPassword }
        }
    }
}
