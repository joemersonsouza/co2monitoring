package com.co2.monitoring.repositories

import com.co2.monitoring.repositories.entities.Sensor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SensorRepository : CrudRepository<Sensor, UUID>
