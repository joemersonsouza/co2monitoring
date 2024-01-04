package com.co2.monitoring.controllers

import com.co2.monitoring.controllers.dto.AlertsResponse
import com.co2.monitoring.controllers.dto.MeasurementRequest
import com.co2.monitoring.controllers.dto.MetricsResponse
import com.co2.monitoring.controllers.dto.StatusResponse
import com.co2.monitoring.services.AlertService
import com.co2.monitoring.services.MetricsService
import com.co2.monitoring.services.SensorService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("sensors")
class SensorsController(
    private val sensorService: SensorService,
    private val metricsService: MetricsService,
    private val alertService: AlertService
) {

    @GetMapping("/{uuid}/status")
    @ResponseStatus(HttpStatus.OK)
    fun getSensorStatus(@PathVariable("uuid") sensorId: UUID): StatusResponse {
        val status = sensorService.getStatusBySensorId(sensorId)
        return StatusResponse(status.name)
    }

    @GetMapping("/{uuid}/metrics")
    @ResponseStatus(HttpStatus.OK)
    fun getSensorMetrics(@PathVariable("uuid") sensorId: UUID): MetricsResponse {
        val metrics = metricsService.getMetricsBySensorId(sensorId)
        return MetricsResponse(metrics.avgMeasurerLast30Days, metrics.maxMeasurerLast30Days)
    }

    @GetMapping("/{uuid}/alerts")
    @ResponseStatus(HttpStatus.OK)
    fun getSensorAlerts(@PathVariable("uuid") sensorId: UUID): List<AlertsResponse> {
        val alerts = alertService.getAlertsBySensorId(sensorId)
        return alerts.map {
            AlertsResponse(
                it.startTime,
                it.endTime,
                it.firstMeasurement,
                it.secondMeasurement ?: 0,
                it.lastMeasurement ?: 0
            )
        }
    }

    @PostMapping("/{uuid}/measurements")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun saveSensorMeasurer(@PathVariable("uuid") sensorId: UUID, @RequestBody request: MeasurementRequest) {
        sensorService.save(sensorId, request.co2, request.time)
    }
}
