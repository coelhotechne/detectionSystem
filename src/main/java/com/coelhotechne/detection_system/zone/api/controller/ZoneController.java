package com.coelhotechne.detection_system.zone.api.controller;

import com.coelhotechne.detection_system.zone.api.dto.ZoneRequest;
import com.coelhotechne.detection_system.zone.api.dto.ZoneResponse;
import com.coelhotechne.detection_system.zone.application.ZoneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Zone")
@RequestMapping(value = "/api/v1/zone",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ZoneController {
    private final ZoneService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ZoneResponse>>findZoneList(){
        return ResponseEntity.status(HttpStatus.OK).body(service.findZoneList());
    }

    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ZoneResponse>findZoneId(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(service.findZoneId(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ZoneResponse>createZone(@Valid @RequestBody ZoneRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createZone(request));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ZoneResponse>updateZone(@PathVariable UUID id,@Valid @RequestBody ZoneRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateZone(id,request));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ZoneResponse>deleteZone(@PathVariable UUID id){
        return ResponseEntity.ok().body(service.deleteZone(id));
    }
}
