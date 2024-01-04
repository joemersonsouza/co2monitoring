package com.co2.monitoring.controllers.dto

import java.time.LocalDateTime

data class AlertsResponse(
    val startTime: LocalDateTime = LocalDateTime.now(),
    val endTime: LocalDateTime? = null,
    val measurement1: Long = 0,
    val measurement2: Long = 0,
    val measurement3: Long = 0
)
