package com.coelhotechne.detection_system.telemetry.api.controller;

import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryRequest;
import com.coelhotechne.detection_system.telemetry.api.dto.TelemetryResponse;
import com.coelhotechne.detection_system.telemetry.application.TelemetryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/telemetry",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Telemetry")
public class TelemetryController {
    private final TelemetryService service;
    // ---- CRUD manual ----
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TelemetryResponse>> findPageTelemetryList(
            @PageableDefault(size = 20, sort = "measuredAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.findTelemetryPageList(pageable));
    }
    @GetMapping(value = "/api/v1/telemetry/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TelemetryResponse>> findAllTelemetryList(){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAllTelemetryList());
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryResponse>findTelemetryId(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(service.findTelemetryId(id));
    }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryResponse>createTelemetryManually(@Valid @RequestBody TelemetryRequest telemetryRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTelemetry(telemetryRequest));
    }
    @PutMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryResponse>updateTelemetryManually(@PathVariable UUID id,@Valid @RequestBody TelemetryRequest telemetryRequest){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateTelemetry(id,telemetryRequest));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<TelemetryResponse>deleteTelemetryManually(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(service.deleteTelemetry(id));
    }
    // Consult from Service
    @GetMapping(value = "/zone/{zoneId}/{telemetryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryResponse> getByZone(@PathVariable UUID zoneId,@Valid @PathVariable UUID telemetryId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findTelemetryWithZone(telemetryId, zoneId));
    }

    @GetMapping(value = "/sensor/{sensorId}/{telemetryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryResponse> getBySensor(@PathVariable UUID sensorId, @PathVariable UUID telemetryId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findTelemetryWithSensor(telemetryId, sensorId));
    }
    @GetMapping(value = "/zone/{zoneId}/sensor/{sensorId}/{telemetryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryResponse> getBySensorAndZone(@PathVariable UUID telemetryId
            ,@PathVariable UUID zoneId
            , @PathVariable UUID sensorId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findTelemetryWithZoneAndSensor(telemetryId,zoneId, sensorId));
    }

    @GetMapping(value = "/sensor/{sensorId}/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TelemetryResponse>> getLatestBySensor(@PathVariable UUID sensorId) {
        return ResponseEntity.ok(service.findLatestBySensor(sensorId));
    }

}
