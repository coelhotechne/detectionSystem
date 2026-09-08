package com.coelhotechne.detection_system.cam.domain.homologation;

public record CamConnectionTestOutcome(
        boolean success,
        String errorMessage,
        String resolvedCodec
) {
    // Retorno de teste de autenticacao
    public static CamConnectionTestOutcome success(String resolvedCodec) {
        return new CamConnectionTestOutcome(true, null, resolvedCodec);
    }
    public static CamConnectionTestOutcome failure(String errorMessage) {
        return new CamConnectionTestOutcome(false, errorMessage, null);
    }
}
