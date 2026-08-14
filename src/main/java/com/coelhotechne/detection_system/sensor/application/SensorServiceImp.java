package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorMapper;
import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.excpetions.SensorNotFoundException;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
@AllArgsConstructor
public class SensorServiceImp implements SensorService {
    private final SensorRepository repository;
    private final SensorMapper mapper;
    @Override
    public List<SensorResponse> findSensorList() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public SensorResponse findSensorId(UUID uuid) {
        return repository.findById(uuid).map(mapper::toResponse).orElseThrow(()->{
            log.error("Sensor with id: {} not find ",uuid);
            throw new SensorNotFoundException(uuid.toString(),null,true,"Sensor not found!");
        });
    }

    @Override
    public SensorResponse createSensor(SensorRequest sensorRequest) {
        Sensor entity = mapper.toEntity(sensorRequest);
        entity.setActivationTime(LocalDateTime.now());
        Sensor created = repository.save(entity);
        return mapper.toResponse(created);
    }

    @Override
    public SensorResponse updateSensor(UUID uuid, SensorRequest sensorRequest) {
        Sensor updated = repository
                .findById(uuid)
                .orElseThrow(()-> new SensorNotFoundException(uuid.toString(),null,true,"Sensor not found!"));
        updated.setName(sensorRequest.name());
        updated.setStatus(sensorRequest.status());
        updated.setActivationTime(sensorRequest.activationTime());
        updated.setMemoryUsed(sensorRequest.memoryUsed());
        updated.setDataTransferValue(sensorRequest.dataTransferValue());
        updated.setDataDescription(sensorRequest.dataDescription());
        updated.setUpdatedAt(LocalDateTime.now());
        Sensor saved = repository.save(updated);
        return mapper.toResponse(saved);
    }

    @Override
    public SensorResponse deleteSensor(UUID uuid) {
        Sensor deleted =repository.findById(uuid).orElseThrow(()->{
            log.error("Sensor id: {} not found to be deleted ",uuid);
            throw new SensorNotFoundException(uuid.toString(),null,true,"Sensor not found!");
        });
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

