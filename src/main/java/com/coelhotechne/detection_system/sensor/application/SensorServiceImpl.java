package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorMapper;
import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.event.SensorEvent;
import com.coelhotechne.detection_system.sensor.event.SensorEventProcessor;
import com.coelhotechne.detection_system.sensor.exceptions.*;
import com.coelhotechne.detection_system.sensor.event.application.MqttSensorClient;
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
    private final MqttSensorClient mqttSensorClient;
    private final SensorEventProcessor eventProcessor;

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

        if (updated.getStatus().isOperational()) {
            throw new SensorStillActiveException(uuid.toString(), true,
                    "Sensor is currently active (%s), deactivate before updating".formatted(updated.getStatus()));
        }


        Zone zone = zoneService.requireZone(sensorRequest.zoneUUID());
        updated.setName(sensorRequest.name());
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
        if (deleted.getStatus().isOperational()) {
            throw new SensorStillActiveException(uuid.toString(), true,
                    "Sensor is currently active (%s), deactivate before deleting".formatted(deleted.getStatus()));
        }

        repository.delete(deleted);
        return mapper.toResponse(deleted);
    }

    @Override
    public void process(SensorEvent event) {
        // transação já acontece dentro de SensorEventProcessor.process — não
        // duplica @Transactional aqui.
        eventProcessor.process(event);
    }


    @Override
    public void sendCommand(UUID uuid, String action) {
        Sensor sensor = repository
                .findById(uuid)
                .orElseThrow(() -> new SensorNotFoundException(uuid.toString(), "Sensor not found!"));

        if (sensor.getZone() == null) {
            throw new SensorWithoutZoneException(uuid.toString(), "Zone from id: " + uuid + " not found!");
        }

        String topic = "home/%s/%s/command".formatted(sensor.getZone().getName(), sensor.getName());

        try {
            mqttSensorClient.publishCommand(topic, action);
        } catch (MqttException e) {
            throw new SensorCommandDeliveryException(uuid, e);
        }
    }

}

