package com.coelhotechne.detection_system.cam.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SdpMetadataExtractor {

    private static final Pattern RTPMAP = Pattern.compile("a=rtpmap:\\d+\\s+([A-Za-z0-9\\-]+)/");

    private SdpMetadataExtractor() {
    }

    static String extractCodec(String sdp) {
        if (sdp == null) {
            return null;
        }
        Matcher matcher = RTPMAP.matcher(sdp);
        return matcher.find() ? matcher.group(1) : null;
    }
}
