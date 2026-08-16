package com.coelhotechne.detection_system.device.application;

import com.coelhotechne.detection_system.device.api.dto.DeviceRequest;
import com.coelhotechne.detection_system.device.api.dto.DeviceResponse;

import java.util.List;
import java.util.UUID;

public interface DeviceService {
    List<DeviceResponse> findDeviceList();
    DeviceResponse findDeviceId(UUID uuid);
    DeviceResponse createDevice(DeviceRequest deviceRequest);
    DeviceResponse updateDevice(UUID uuid,DeviceRequest deviceRequest);
    DeviceResponse deleteDevice(UUID uuid);
}
