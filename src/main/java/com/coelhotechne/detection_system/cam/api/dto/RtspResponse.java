package com.coelhotechne.detection_system.cam.api.dto;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public record RtspResponse(
        int statusCode,
        Map<String, List<String>> headers,
        String body
) {
    public List<String> header(String name) {
        return headers.getOrDefault(name.toLowerCase(Locale.ROOT),List.of());
    }
}
