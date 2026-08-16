package com.coelhotechne.detection_system.device.api.controller.auth;

import com.coelhotechne.detection_system.device.api.dto.auth.DeviceAuthRequest;
import com.coelhotechne.detection_system.device.api.dto.auth.DeviceAuthResponse;
import com.coelhotechne.detection_system.device.application.auth.DeviceAuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/device",produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class DeviceAuthController {

    private final DeviceAuthService authService;

    @PostMapping(value = "/{id}/authenticate",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceAuthResponse> authenticate(@PathVariable UUID id, @Valid @RequestBody DeviceAuthRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.authenticate(id,request.accessKey()));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{id}/rotate-key")
    public ResponseEntity<String>rotateAccessKey(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(authService.rotateAccessKey(id));
    }
}
