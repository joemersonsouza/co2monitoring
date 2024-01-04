package com.co2.monitoring.repositories.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.util.*

@Entity
data class SensorMeasurement(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column
    val value: Long = 0,
    @ManyToOne
    val sensor: Sensor = Sensor()
)
