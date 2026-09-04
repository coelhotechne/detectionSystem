package com.coelhotechne.detection_system.telemetry.infrastructure;

import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TelemetryRepository extends JpaRepository<Telemetry, UUID> {
}
