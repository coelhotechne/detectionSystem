package com.coelhotechne.detection_system.cam.api.controller.auth;

import com.coelhotechne.detection_system.cam.api.dto.auth.CamAuthRequest;
import com.coelhotechne.detection_system.cam.api.dto.auth.CamAuthResponse;
import com.coelhotechne.detection_system.cam.application.auth.CamAuthService;
import com.coelhotechne.detection_system.cam.application.auth.enums.CamAuthMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cam")
@RequiredArgsConstructor
public class CamAuthController {

    private final CamAuthService camAuthService;

    @PostMapping("/{camId}/auth")
    public ResponseEntity<CamAuthResponse> authenticate(@PathVariable UUID camId, @RequestBody CamAuthRequest request) {
        CamAuthResponse response = camAuthService.authenticate(camId, request.accessKey());
        HttpStatus status = response.message() == CamAuthMessage.AUTORIZADO ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}
