package com.co2.monitoring.repositories.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Alert(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    @ManyToOne(fetch = FetchType.LAZY)
    val sensor: Sensor = Sensor(),
    @Column(nullable = false)
    val startTime: LocalDateTime = LocalDateTime.now(),
    @Column
    val endTime: LocalDateTime? = null,
    @Column(nullable = false)
    val firstMeasurement: Long = 0,
    @Column
    val secondMeasurement: Long? = null,
    @Column
    val lastMeasurement: Long? = null,
)
