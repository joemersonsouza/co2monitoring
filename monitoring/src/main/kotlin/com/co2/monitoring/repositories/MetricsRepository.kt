package com.co2.monitoring.repositories

import com.co2.monitoring.repositories.entities.Metrics
import com.co2.monitoring.repositories.entities.Sensor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MetricsRepository : CrudRepository<Metrics, UUID>{
    fun findBySensor(sensor: Sensor): Optional<Metrics>
    fun findBySensorUuid(sensorId: UUID): Optional<Metrics>
}
