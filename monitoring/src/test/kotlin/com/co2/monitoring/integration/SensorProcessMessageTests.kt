package com.co2.monitoring.integration

import com.co2.monitoring.controllers.dto.AlertsResponse
import com.co2.monitoring.controllers.dto.MeasurementRequest
import com.co2.monitoring.controllers.dto.MetricsResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Testcontainers
class SensorProcessMessageTests : BaseIntegrationTests() {

    private lateinit var sensorId: UUID

    @BeforeEach
    fun setup() {
        mapper.registerModule(JavaTimeModule())
        saveSensor()
        sensorId = sensor.uuid
    }

    @Test
    fun `Find a sensor status that does not exist`() {
        val uuid = UUID.randomUUID()
        val path = "/sensors/$uuid/status"
        val requestBuilder = MockMvcRequestBuilders.get(path)
        requestBuilder.contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `Find a sensor status`() {
        val path = "/sensors/$sensorId/status"
        val requestBuilder = MockMvcRequestBuilders.get(path)
        requestBuilder.contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Find a sensor metrics`() {
        val metricDb = saveMetrics()
        val path = "/sensors/$sensorId/metrics"
        val requestBuilder = MockMvcRequestBuilders.get(path)
        requestBuilder.contentType(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val metric = mapper.readValue(result.response.contentAsString, MetricsResponse::class.java)
        assertEquals(metricDb.maxMeasurerLast30Days, metric.maxLast30Days)
    }

    @Test
    fun `Find a sensor Alerts`() {
        saveAlerts()
        val path = "/sensors/$sensorId/alerts"
        val requestBuilder = MockMvcRequestBuilders.get(path)
        requestBuilder.contentType(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val metric =
            mapper.readValue(result.response.contentAsString, object : TypeReference<List<AlertsResponse>>() {})
        assertTrue(metric.isNotEmpty())
        assertNotNull(metric[0].measurement1)
    }

    @Test
    fun `Create a new measure for a sensor`() {
        val measurement = MeasurementRequest(co2 = 2500L, LocalDateTime.now())
        val path = "/sensors/$sensorId/measurements"
        val requestBuilder = MockMvcRequestBuilders.post(path)
        requestBuilder.contentType(MediaType.APPLICATION_JSON)
        requestBuilder.content(mapper.writeValueAsString(measurement))

        mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isAccepted)
    }
}
