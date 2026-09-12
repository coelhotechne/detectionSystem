package com.coelhotechne.detection_system.cam.api.dto.fixed;

import com.coelhotechne.detection_system.cam.domain.connectioncam.CamConnectionProfile;
import com.coelhotechne.detection_system.cam.domain.connectioncam.enums.CamCapability;
import com.coelhotechne.detection_system.cam.domain.connectioncam.enums.CamProtocol;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.Set;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CamConnectionSummary(
        CamProtocol protocol,
        String streamUri,
        String host,
        Integer port,
        String vendorSdkId,
        Set<CamCapability> capabilities,
        CamNetworkLinkSummary networkLink
) {
    public static CamConnectionSummary from(CamConnectionProfile profile) {
        if (profile == null) {
            return null;
        }
        return new CamConnectionSummary(
                profile.getProtocol(),
                profile.getStreamUri(),
                profile.getHost(),
                profile.getPort(),
                profile.getVendorSdkId(),
                profile.getCapabilities(),
                CamNetworkLinkSummary.from(profile.getNetworkLink())
        );
    }
}