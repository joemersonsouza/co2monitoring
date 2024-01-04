package com.co2.monitoring.repositories.entities

import com.co2.monitoring.repositories.SensorStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDateTime
import java.util.*

@Entity
data class Sensor(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val status: SensorStatus = SensorStatus.OK,
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
