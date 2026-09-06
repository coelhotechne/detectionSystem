package com.coelhotechne.detection_system.cam.domain.connectioncam;

import com.coelhotechne.detection_system.cam.domain.connectioncam.enums.CamCapability;
import com.coelhotechne.detection_system.connection.domain.enums.LinkType;

import java.util.Set;

public record CamConnectionParams(
        String streamUri, String host, Integer port,
        String vendorSdkId, String credentialRef,
        LinkType linkType, String ipAddress, Integer signalStrengthDbm,
        Set<CamCapability> capabilities
) {
}