package com.coelhotechne.detection_system.cam.application.auth;

import com.coelhotechne.detection_system.cam.api.dto.auth.CamAuthResponse;

import java.util.UUID;
public interface CamAuthService {
    // Usado pelo endpoint público de autenticação da câmera
    CamAuthResponse authenticate(UUID camId, String presentedAccessKey);
    // Guarda interna pra outros fluxos que exigem uma accessKey válida sem montar resposta
    // HTTP diretamente — mesmo padrão de DeviceAuthService.requireValidAccessKey().
    void requireValidAccessKey(UUID camId, String presentedAccessKey);
}