package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorMapper;
import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.api.dto.SensorTelemetryPayload;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.exceptions.*;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import com.coelhotechne.detection_system.zone.application.ZoneService;
import com.coelhotechne.detection_system.zone.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
@AllArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final SensorRepository repository;
    private final SensorMapper mapper;
    private final ZoneService zoneService;
    private MqttSensorClient mqttSensorClient;
    @Override
    @Transactional(readOnly = true)
    public List<SensorResponse> findSensorList() {
        return repository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SensorResponse findSensorId(UUID uuid) {
        return repository
                .findById(uuid)
                .map(mapper::toResponse)
                .orElseThrow(()->{
            log.error("Sensor with id: {} not find ",uuid);
            return new SensorNotFoundException(uuid.toString(),"Sensor not found!");
        });
    }

    @Override
    @Transactional
    public SensorResponse createSensor(SensorRequest sensorRequest) {
        Sensor entity = mapper.toEntity(sensorRequest);
        Zone zone = zoneService.requireZone(sensorRequest.zoneUUID());
        entity.setActivationTime(LocalDateTime.now());
        entity.setInstallation(sensorRequest.installation());
        entity.setZone(zone);
        Sensor created = repository.save(entity);
        return mapper.toResponse(created);
    }

    @Override
    @Transactional
    public SensorResponse updateSensor(UUID uuid, SensorRequest sensorRequest) {
        Sensor updated = repository
                .findById(uuid)
                .orElseThrow(()-> new SensorNotFoundException(uuid.toString(),"Sensor not found!"));
        boolean wasActive = updated.getSensorStatus();
        boolean staysActive = sensorRequest.sensorStatus();
        if (wasActive && staysActive){
            throw new SensorStillActiveException(uuid.toString(),updated.getSensorStatus(),"Sensor is currently active, deactivate before updating");
        }
        Zone zone = zoneService.requireZone(sensorRequest.zoneUUID());
        updated.setName(sensorRequest.name());
        updated.setSensorStatus(staysActive);
        updated.setMemoryUsed(sensorRequest.memoryUsed());
        updated.setDataTransferValue(sensorRequest.dataTransferValue());
        updated.setDataDescription(sensorRequest.dataDescription());
        updated.setZone(zone);
        try {
            Sensor saved = repository.saveAndFlush(updated);
            return mapper.toResponse(saved);
        }catch (ObjectOptimisticLockingFailureException ex){
            throw new SensorConcurrentModificationException(uuid.toString(),ex);
        }
    }

    @Override
    @Transactional
    public SensorResponse deleteSensor(UUID uuid) {
        Sensor deleted =repository.findById(uuid).orElseThrow(()->{
            log.error("Sensor id: {} not found to be deleted ",uuid);
            return new SensorNotFoundException(uuid.toString(),"Sensor not found!");
        });
        if (deleted.getSensorStatus()){
            throw new SensorStillActiveException(uuid.toString(),deleted.getSensorStatus(),
                    "Sensor is currently active, deactivate before deleting");
        }

        repository.delete(deleted);
        return mapper.toResponse(deleted);
    }

    @Override
    @Transactional
    public void applyTelemetry(String zoneName, String sensorName, SensorTelemetryPayload payload,Boolean sensorStatus) {
        Sensor sensor = repository.findByNameAndZoneName(sensorName, zoneName)
                .orElseThrow(() -> new SensorNotFoundException(sensorName, zoneName));

        sensor.setSensorStatus(sensorStatus);
        sensor.setMemoryUsed(payload.memoryUsed());
        sensor.setDataTransferValue(payload.dataTransferValue());
        sensor.setDataDescription(payload.dataDescription());

        repository.saveAndFlush(sensor);
    }

    @Override
    public void sendCommand(UUID uuid, String action) {
        Sensor sensor = repository
                .findById(uuid)
                .orElseThrow(()-> new SensorNotFoundException(uuid.toString(),"Sensor not found!"));

        if (sensor.getZone() == null) {
            throw new SensorWithoutZoneException(uuid.toString(),"Zone from id: "+uuid.toString()+" not found!"); // zone é opcional na entidade
        }

        String topic = "home/%s/%s/command".formatted(sensor.getZone().getName(), sensor.getName());

        try {
            mqttSensorClient.publishCommand(topic, action);
        } catch (MqttException e) {
            throw new SensorCommandDeliveryException(uuid, e);
        }
    }
}

