package com.coelhotechne.detection_system.telemetry.infrastructure;

import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TelemetryRepository extends JpaRepository<Telemetry, UUID> {
    List<Telemetry> findBySensorUuidOrderByMeasuredAtDesc(UUID sensorId);

    //histórico por zona, mesmo motivo acima
    List<Telemetry> findByZone_UuidOrderByMeasuredAtDesc(UUID zoneId);

    // consulta por janela de tempo, útil para gráficos/relatórios
    List<Telemetry> findBySensorUuidAndMeasuredAtBetween(UUID sensorId, Instant start, Instant end);
}
