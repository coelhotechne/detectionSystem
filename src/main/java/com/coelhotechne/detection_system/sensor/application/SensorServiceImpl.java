package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorMapper;
import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.exceptions.SensorConcurrentModificationException;
import com.coelhotechne.detection_system.sensor.exceptions.SensorStillActiveException;
import com.coelhotechne.detection_system.sensor.exceptions.SensorNotFoundException;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import com.coelhotechne.detection_system.zone.application.ZoneService;
import com.coelhotechne.detection_system.zone.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
        entity.setInstallationDate(LocalDateTime.now());
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
        boolean wasActive = updated.isStatus();
        boolean staysActive = sensorRequest.status();
        if (wasActive && staysActive){
            throw new SensorStillActiveException(uuid.toString(),updated.isStatus(),"Sensor is currently active, deactivate before updating");
        }
        Zone zone = zoneService.requireZone(sensorRequest.zoneUUID());
        updated.setName(sensorRequest.name());
        updated.setStatus(staysActive);
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
        if (deleted.isStatus()){
            throw new SensorStillActiveException(uuid.toString(),deleted.isStatus(),
                    "Sensor is currently active, deactivate before deleting");
        }

        repository.delete(deleted);
        return mapper.toResponse(deleted);
    }
/*
    @Override
    public void atualizarSensores(UUID uuid, Map<SensoresTipo, Boolean> novosEstados) {
        Sensor sensor= repository
                .findById(uuid)
                .orElseThrow(()->new ModelNotFoundException("ID: "+uuid+" not found!"));
        if (sensor.getSensorTipo()==null){
            sensor.setSensorTipo(new HashMap<>());
        }
        novosEstados.forEach(((sensoresTipo, estado) ->{
            sensor.getSensorTipo().put(sensoresTipo,estado);
        } ));
        repository.save(sensor);
        log.info("Tipos de sensores com ID: {} atualizados com sucesso ",uuid);
    }

    @Override
    public void atualizarRegioes(UUID uuid,Map<Regiao, Boolean> novasRegioes) {
        Sensor sensor = repository
                .findById(uuid)
                .orElseThrow(() -> new ModelNotFoundException("Sensor com ID " + uuid + " não encontrado"));

        if (sensor.getZone() == null) {
            sensor.setZone(new HashMap<>());
        }
        novasRegioes.forEach((regiao, estado) -> {
            sensor.getZone().put(regiao, estado);
        });
        repository.save(sensor);
        log.info("Regiões do sensor {} atualizadas com sucesso.", uuid);
    }*/
}

