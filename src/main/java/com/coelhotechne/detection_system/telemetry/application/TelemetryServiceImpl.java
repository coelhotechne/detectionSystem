package com.coelhotechne.detection_system.telemetry.application;

import com.coelhotechne.detection_system.sensor.application.SensorService;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryMapper;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryRequest;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryResponse;
import com.coelhotechne.detection_system.telemetry.domain.Telemetry;
import com.coelhotechne.detection_system.telemetry.exceptions.TelemetryConcurrentModificationException;
import com.coelhotechne.detection_system.telemetry.exceptions.TelemetryNotFoundException;
import com.coelhotechne.detection_system.telemetry.exceptions.TelemetryNullException;
import com.coelhotechne.detection_system.telemetry.infrastructure.TelemetryRepository;
import com.coelhotechne.detection_system.zone.application.ZoneService;
import com.coelhotechne.detection_system.zone.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    @Override
    public Telemetry requireTelemetry(UUID telemetryId) {
        if (telemetryId == null){
            throw new TelemetryNullException("null","Cannot find telemetry, because it is is null");
        }
        return repository.findById(telemetryId)
                .orElseThrow(() -> new TelemetryNotFoundException(telemetryId.toString(),"Telemetry not found."));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<TelemetryResponse> findTelemetryPageList(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
    // user setter, get and read the values ->
    @Override
    @Transactional(readOnly = true)
    public List<TelemetryResponse> findAllTelemetryList() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TelemetryResponse findTelemetryId(UUID uuid) {
        return mapper.toResponse(requireTelemetry(uuid));
    }

    @Override
    @Transactional
    public TelemetryResponse createTelemetry(TelemetryRequest telemetryRequest) {
        Zone zoneEntity = zoneService.requireZone(telemetryRequest.zoneId());
        Sensor sensorEntity = sensorService.requireSensor(telemetryRequest.sensorId());

        Telemetry entity = mapper.toEntity(telemetryRequest);
        entity.setZone(zoneEntity);
        entity.setSensor(sensorEntity);
        entity.setMeasuredAt(telemetryRequest.measuredAt()!=null ? telemetryRequest.measuredAt() : Instant.now());
        Telemetry saved = repository.save(entity);
        log.info("Telemetry {} created manually for sensor {} in zone {}",saved.getUuid(),saved.getSensor(),saved.getZone());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TelemetryResponse updateTelemetry(UUID uuid, TelemetryRequest telemetryRequest) {
        Telemetry updated = requireTelemetry(uuid);

        Zone zoneEntity = zoneService.requireZone(telemetryRequest.zoneId());
        Sensor sensorEntity = sensorService.requireSensor(telemetryRequest.sensorId());

        updated.setSensor(sensorEntity);
        updated.setZone(zoneEntity);
        updated.setMeasuredAt(telemetryRequest.measuredAt()!=null ? telemetryRequest.measuredAt() : Instant.now());
        updated.setMeasuredValue(telemetryRequest.measuredValue());
        try {
            Telemetry saved=repository.save(updated);
            log.info("Telemetry {} updated manually", saved.getUuid());
            return mapper.toResponse(saved);
        }catch (ObjectOptimisticLockingFailureException ex){
            throw new TelemetryConcurrentModificationException
                    (uuid.toString(),"Telemetry '%s' was modified currenttly, reload and try again"
                            .formatted(uuid));
        }
    }

    @Override
    @Transactional
    public TelemetryResponse deleteTelemetry(UUID uuid) {
        Telemetry deleted = requireTelemetry(uuid);
        repository.delete(deleted);
        log.info("Telemetry {} deleted manually", uuid);
        return mapper.toResponse(deleted);
    }

    //Automatic find, read and get values
    @Override
    @Transactional(readOnly = true)
    public TelemetryResponse findTelemetryWithSensor(UUID telemetryId, UUID sensorId) {
        Telemetry telemetry = requireTelemetry(telemetryId);
        requireBelongsToSensor(telemetry,sensorId);
        return mapper.toResponse(telemetry);
    }

    @Override
    @Transactional(readOnly = true)
    public TelemetryResponse findTelemetryWithZone(UUID telemetryId, UUID zoneid) {
        Telemetry telemetry = requireTelemetry(telemetryId);
        requireBelongsToZone(telemetry,zoneid);
        return mapper.toResponse(telemetry);
    }

    @Override
    @Transactional(readOnly = true)
    public TelemetryResponse findTelemetryWithZoneAndSensor(UUID telemetryId, UUID zoneid, UUID sensorId) {
        Telemetry telemetry = requireTelemetry(telemetryId);
        requireBelongsToZone(telemetry, zoneid);
        requireBelongsToSensor(telemetry, sensorId);
        return mapper.toResponse(telemetry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelemetryResponse> findLatestBySensor(UUID sensorId) {
        sensorService.requireSensor(sensorId);
        return repository.findBySensorUuidOrderByMeasuredAtDesc(sensorId).stream()
                .map(mapper::toResponse)
                .toList();
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
