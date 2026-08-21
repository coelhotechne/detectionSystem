package com.coelhotechne.detection_system.location.application;

import com.coelhotechne.detection_system.location.api.dto.LocationRequest;
import com.coelhotechne.detection_system.location.api.dto.LocationResponse;

import java.util.List;
import java.util.UUID;

public interface LocationService {
    List<LocationResponse>findLocationList();
    LocationResponse findLocationId(UUID uuid);
    LocationResponse createLocation(LocationRequest locationRequest);
    LocationResponse updateLocation(UUID uuid, LocationRequest locationRequest);
    LocationResponse deleteLocation(UUID uuid);
}
