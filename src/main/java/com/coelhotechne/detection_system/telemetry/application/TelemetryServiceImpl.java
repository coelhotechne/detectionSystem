package com.coelhotechne.detection_system.telemetry.application;

import com.coelhotechne.detection_system.sensor.application.SensorEventProcessor;
import com.coelhotechne.detection_system.sensor.application.SensorService;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.mqtt.MqttSensorClient;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryMapper;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryRequest;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryResponse;
import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import com.coelhotechne.detection_system.telemetry.exceptions.TelemetryNotFoundException;
import com.coelhotechne.detection_system.telemetry.infrastructure.TelemetryRepository;
import com.coelhotechne.detection_system.zone.application.ZoneService;
import com.coelhotechne.detection_system.zone.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class TelemetryServiceImpl implements TelemetryService{
    private final TelemetryRepository repository;
    private final TelemetryMapper mapper;
    private final ZoneService zoneService;
    private final SensorService sensorService;
    private final MqttSensorClient mqttSensorClient;
    private final SensorEventProcessor eventProcessor;

    @Override
    public Telemetry requireTelemetry(UUID telemetryId) {
        if (telemetryId == null){
            throw new NullPointerException("Telemetry cannot be null");
        }
        return repository.findById(telemetryId)
                .orElseThrow(() -> new TelemetryNotFoundException(telemetryId.toString(),"Telemetry not found."));
    }

    // user setter, get and read the values ->
    @Override
    @Transactional(readOnly = true)
    public List<TelemetryResponse> findTelemetryList() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TelemetryResponse findTelemetryId(UUID uuid) {
        return mapper.toResponse(requireTelemetry(uuid));
    }

    @Override
    public TelemetryResponse createTelemetry(TelemetryRequest telemetryRequest) {
        Telemetry entity = mapper.toEntity(telemetryRequest);
        Zone zoneEntity = zoneService.requireZone(telemetryRequest.zoneId());
        Sensor sensorEntity = sensorService.requireSensor(telemetryRequest.sensorId());
        entity.setZone(zoneEntity);
        entity.setSensor(sensorEntity);
        entity.setMeasuredAt(Instant.now());
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    public TelemetryResponse updateTelemetry(UUID uuid, TelemetryRequest telemetryRequest) {
        Telemetry updated = repository
                .findById(uuid).orElseThrow(()-> new TelemetryNotFoundException(uuid.toString(),"Id not found!"));

        Zone zoneEntity = zoneService.requireZone(telemetryRequest.zoneId());
        Sensor sensorEntity = sensorService.requireSensor(telemetryRequest.sensorId());
        updated.setSensor(sensorEntity);
        updated.setZone(zoneEntity);
        updated.setMeasuredAt(Instant.now());
        updated.setMeasuredValue(telemetryRequest.measuredValue());
        repository.save(updated);
        return mapper.toResponse(updated);
    }

    @Override
    public TelemetryResponse deleteTelemetry(UUID uuid) {
        Telemetry deleted = repository
                .findById(uuid).orElseThrow(()-> new TelemetryNotFoundException(uuid.toString(),"Id not found!"));
        repository.delete(deleted);
        return mapper.toResponse(deleted);
    }

    //Automatic find, read and get values
    @Override
    public TelemetryResponse findTelemetryWithSensor(UUID telemetryId, UUID sensorId) {
        Telemetry telemetry = requireTelemetry(telemetryId);
        requireBelongsToSensor(telemetry,sensorId);
        return mapper.toResponse(telemetry);
    }

    @Override
    public TelemetryResponse findTelemetryWithZone(UUID telemetryId, UUID zoneid) {
        Telemetry telemetry = requireTelemetry(telemetryId);
        requireBelongsToZone(telemetry,zoneid);
        return mapper.toResponse(telemetry);
    }

    @Override
    public TelemetryResponse findTelemetryWithZoneAndSensor(UUID telemetryId, UUID zoneid, UUID sensorId) {
        return null;
    }
    private void requireBelongsToSensor(Telemetry telemetry, UUID sensorId) {
        if (!telemetry.getSensor().getUuid().equals(sensorId)) {
            throw new TelemetryNotFoundException(telemetry.getUuid().toString(),
                    "Telemetry '%s' does not belong to sensor '%s'".formatted(telemetry.getUuid(), sensorId));
        }
    }

    private void requireBelongsToZone(Telemetry telemetry, UUID zoneId) {
        if (!telemetry.getZone().getUuid().equals(zoneId)) {
            throw new TelemetryNotFoundException(telemetry.getUuid().toString(),
                    "Telemetry '%s' does not belong to zone '%s'".formatted(telemetry.getUuid(), zoneId));
        }
    }


}
