package com.coelhotechne.detection_system.telemetry.application;

import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryRequest;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryResponse;
import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TelemetryService {
    Telemetry requireTelemetry(UUID telemetryId);
    Page<TelemetryResponse> findTelemetryPageList(Pageable pageable);
    List<TelemetryResponse> findAllTelemetryList();
    TelemetryResponse findTelemetryId(UUID uuid);
    TelemetryResponse createTelemetry(TelemetryRequest telemetryRequest);
    TelemetryResponse updateTelemetry(UUID uuid,TelemetryRequest telemetryRequest);
    TelemetryResponse deleteTelemetry(UUID uuid);
    TelemetryResponse findTelemetryWithSensor(UUID telemetryId,UUID sensorId);
    TelemetryResponse findTelemetryWithZone(UUID telemetryId,UUID zoneid);
    TelemetryResponse findTelemetryWithZoneAndSensor(UUID telemetryId,UUID zoneid, UUID sensorId);
    List<TelemetryResponse> findLatestBySensor(UUID sensorId);
}
