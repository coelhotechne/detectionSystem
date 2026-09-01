package com.coelhotechne.detection_system.sensor.infrastructure;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {
    Optional<Sensor> findByName(String name);
    Optional<Sensor>findByNameAndZoneName(String sensorName, String zoneName);
}