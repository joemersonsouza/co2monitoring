package com.co2.monitoring.repositories.entities

import jakarta.persistence.*
import java.util.*

@Entity
data class Metrics(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    @OneToOne(fetch = FetchType.LAZY)
    val sensor: Sensor = Sensor(),
    @Column(nullable = false)
    val maxMeasurerLast30Days: Long = 0,
    @Column(nullable = false)
    val avgMeasurerLast30Days: Double = 0.00
)
