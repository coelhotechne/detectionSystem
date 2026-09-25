package com.coelhotechne.detection_system.cam.infrastructure.protocol;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SdpMetadataExtractor {

    private static final Pattern RTPMAP = Pattern.compile("^a=rtpmap:\\d+\\s+([A-Za-z0-9\\-]+)/");

    // Payloads estáticos de vídeo (RFC 3551) — não exigem a=rtpmap
    private static final Map<String, String> STATIC_VIDEO_PAYLOADS = Map.of(
            "26", "JPEG",
            "32", "MPV",
            "33", "MP2T",
            "34", "H263");

    private SdpMetadataExtractor() {
    }

    static String extractCodec(String sdp) {
        if (sdp == null) {
            return null;
        }
        boolean inVideo = false;
        String firstPayload = null;

        for (String raw : sdp.split("\\r?\\n")) {
            String line = raw.trim();
            if (line.startsWith("m=")) {
                if (inVideo) {
                    break; // a seção de vídeo terminou sem rtpmap
                }
                inVideo = line.startsWith("m=video");
                if (inVideo) {
                    // ex.: "m=video 0 RTP/AVP 96" → payload na 4ª posição
                    String[] parts = line.split("\\s+");
                    firstPayload = parts.length > 3 ? parts[3] : null;
                }
                continue;
            }
            if (inVideo) {
                Matcher matcher = RTPMAP.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return firstPayload == null ? null : STATIC_VIDEO_PAYLOADS.get(firstPayload);
    }
}