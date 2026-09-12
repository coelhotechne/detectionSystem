package com.coelhotechne.detection_system.cam.api.dto.fixed;

import com.coelhotechne.detection_system.connection.domain.NetworkLink;
import com.coelhotechne.detection_system.connection.domain.enums.LinkType;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CamNetworkLinkSummary(
        LinkType linkType,
        String ipAddress,
        Integer signalStrengthDbm
) {
    public static CamNetworkLinkSummary from(NetworkLink link) {
        if (link == null) {
            return null;
        }
        return new CamNetworkLinkSummary(link.getLinkType(), link.getIpAddress(), link.getSignalStrengthDbm());
    }
}

