package com.coelhotechne.detection_system.cam.application.auth;

import com.coelhotechne.detection_system.cam.api.dto.auth.CamAuthResponse;
import com.coelhotechne.detection_system.cam.application.auth.enums.CamAuthMessage;
import com.coelhotechne.detection_system.cam.domain.base.BaseCam;
import com.coelhotechne.detection_system.cam.exceptions.CamAuthenticationException;
import com.coelhotechne.detection_system.cam.infrastructure.CamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CamAuthServiceImpl implements CamAuthService {

    private final CamRepository camRepository;

    @Override
    @Transactional
    public CamAuthResponse authenticate(UUID camId, String presentedAccessKey) {
        Optional<BaseCam> maybeCam = camRepository.findById(camId);
        if (maybeCam.isEmpty()) {
            return new CamAuthResponse(camId, CamAuthMessage.NEGADO, null);
        }
        BaseCam cam = maybeCam.get();
        if (!matches(cam.getAccessKey(), presentedAccessKey)) {
            return new CamAuthResponse(camId, CamAuthMessage.NEGADO, null);
        }
        Instant now = Instant.now();
        cam.recordCommunication(now);
        return new CamAuthResponse(camId, CamAuthMessage.AUTORIZADO, now);
    }

    @Override
    public void requireValidAccessKey(UUID camId, String presentedAccessKey) {
        CamAuthResponse response = authenticate(camId, presentedAccessKey);
        if (response.message() != CamAuthMessage.AUTORIZADO) {
            throw new CamAuthenticationException(camId);
        }
    }

    // Comparação em tempo constante — evita side-channel timing attack sobre a accessKey.
    private boolean matches(String stored, String presented) {
        if (stored == null || presented == null) {
            return false;
        }
        return MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                presented.getBytes(StandardCharsets.UTF_8)
        );
    }
}
