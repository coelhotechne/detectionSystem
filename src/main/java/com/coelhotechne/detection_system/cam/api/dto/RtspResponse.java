package com.coelhotechne.detection_system.cam.api.dto;

import java.util.Locale;
import java.util.Map;

public record RtspResponse(
        int statusCode,
        Map<String, String> headers,
        String body
) {
    public String header(String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }
}
