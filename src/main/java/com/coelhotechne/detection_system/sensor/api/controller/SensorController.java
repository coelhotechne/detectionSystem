package com.coelhotechne.detection_system.sensor.api.controller;

import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.application.SensorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/sensor",produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class SensorController {
    private final SensorService sensorService;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SensorResponse>> findSensorList(){
        return ResponseEntity.status(HttpStatus.OK).body(sensorService.findSensorList());
    }
    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorResponse>findSensorId(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(sensorService.findSensorId(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorResponse> createSensor(@RequestBody SensorRequest sensorRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.createSensor(sensorRequest));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorResponse> updateSensor(@PathVariable UUID id,@RequestBody SensorRequest sensorRequest){
        return ResponseEntity.status(HttpStatus.OK).body(sensorService.updateSensor(id, sensorRequest));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SensorResponse> deleteSensor(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(sensorService.deleteSensor(id));
    }

    /*
    @PatchMapping(value = "/{id}/sensores-tipo",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>atualizarSensoresTipo(@PathVariable UUID id, @RequestBody Map<SensoresTipo,Boolean> novosEstados){
        sensorService.atualizarSensores(id,novosEstados);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(value = "/{id}/regiao",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>atualizarRegiao(@PathVariable UUID id, @RequestBody Map<Regiao,Boolean>novasRegioes){
        sensorService.atualizarRegioes(id,novasRegioes);
        return ResponseEntity.noContent().build();
    }
*/
}
