package com.coelhotechne.detection_system.zone.application;

import com.coelhotechne.detection_system.zone.api.dto.ZoneRequest;
import com.coelhotechne.detection_system.zone.api.dto.ZoneResponse;

import java.util.List;
import java.util.UUID;

public interface ZoneService {
    List<ZoneResponse>findZoneList();
    ZoneResponse findZoneId(UUID uuid);
    ZoneResponse createZone(ZoneRequest zoneRequest);
    ZoneResponse updateZone(UUID uuid, ZoneRequest zoneRequest);
    ZoneResponse deleteZone(UUID uuid);
}
