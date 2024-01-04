package com.co2.monitoring.controllers.dto

import java.time.LocalDateTime

data class MeasurementRequest(
    val co2: Long,
    val time: LocalDateTime
)
