package com.co2.monitoring.controllers.dto

data class MetricsResponse(
    val avgLast30Days: Double = 0.0,
    val maxLast30Days: Long = 0
)
