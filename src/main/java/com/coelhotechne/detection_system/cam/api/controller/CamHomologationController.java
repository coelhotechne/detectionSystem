package com.coelhotechne.detection_system.cam.api.controller;

import com.coelhotechne.detection_system.cam.api.dto.auth.CamHomologationDecisionRequest;
import com.coelhotechne.detection_system.cam.application.CamHomologationService;
import com.coelhotechne.detection_system.cam.domain.homologation.CamHomologationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cam")
@RequiredArgsConstructor
public class CamHomologationController {

    private final CamHomologationService camHomologationService;

    @PostMapping("/{camId}/homologation/test")
    public ResponseEntity<CamHomologationRecord> runConnectionTest(@PathVariable UUID camId) {
        return ResponseEntity.ok(camHomologationService.runConnectionTest(camId));
    }

    @PostMapping("/{camId}/homologation/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CamHomologationRecord> approve(@PathVariable UUID camId, Authentication authentication) {
        return ResponseEntity.ok(camHomologationService.approve(camId, authentication.getName()));
    }

    @PostMapping("/{camId}/homologation/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CamHomologationRecord> reject(@PathVariable UUID camId,
                                                        @RequestBody CamHomologationDecisionRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(camHomologationService.reject(camId, authentication.getName(), request.reason()));
    }
}
