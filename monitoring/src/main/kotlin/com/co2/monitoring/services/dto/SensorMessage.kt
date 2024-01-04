package com.co2.monitoring.services.dto

import java.sql.Timestamp
import java.time.Instant
import java.util.*

data class SensorMessage(
    val sensorId: UUID = UUID.randomUUID(),
    val co2: Long = 0L,
    val createdAt: Timestamp = Timestamp.from(Instant.now())
)
