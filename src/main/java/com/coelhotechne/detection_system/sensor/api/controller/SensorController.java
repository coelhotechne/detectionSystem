package com.coelhotechne.detection_system.sensor.api.controller;

import com.coelhotechne.detection_system.sensor.api.dto.SensorRequest;
import com.coelhotechne.detection_system.sensor.api.dto.SensorResponse;
import com.coelhotechne.detection_system.sensor.application.SensorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Sensor")
@CrossOrigin(origins = "http://localhost:5173") // ou o IP/host da UI em produção
@RequestMapping(value = "/api/v1/sensors",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
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
}
