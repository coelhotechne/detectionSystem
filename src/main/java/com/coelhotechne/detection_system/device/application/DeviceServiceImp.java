package com.coelhotechne.detection_system.device.application;

import com.coelhotechne.detection_system.device.api.dto.DeviceMapper;
import com.coelhotechne.detection_system.device.api.dto.DeviceRequest;
import com.coelhotechne.detection_system.device.api.dto.DeviceResponse;
import com.coelhotechne.detection_system.device.api.dto.auth.DeviceKeyGenerator;
import com.coelhotechne.detection_system.device.domain.Device;
import com.coelhotechne.detection_system.device.exceptions.DeviceNotFoundException;
import com.coelhotechne.detection_system.device.infrastructure.DeviceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Log4j2
public class DeviceServiceImp implements DeviceService{
    private final DeviceRepository repository;
    private final DeviceMapper mapper;
    private final DeviceKeyGenerator keyGenerator;
    @Override
    public List<DeviceResponse> findDeviceList() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public DeviceResponse findDeviceId(UUID uuid) {
        return repository.findById(uuid).map(mapper::toResponse).orElseThrow(()->{
            log.error("Device id not found {}",uuid);
            throw new DeviceNotFoundException(
                    uuid.toString(),
                    null,
                    null,
                    "Device not found!"
            );
        });
    }

    @Override
    public DeviceResponse createDevice(DeviceRequest deviceRequest) {
        Device entity = mapper.toEntity(deviceRequest);
        entity.setAccessKey(keyGenerator.generate());
        entity.setLastCommunication(LocalDateTime.now());
        Device create = repository.save(entity);
        return mapper.toResponse(create);
    }

    @Override
    public DeviceResponse updateDevice(UUID uuid, DeviceRequest deviceRequest) {

        Device update=repository
                .findById(uuid)
                .orElseThrow(()-> new DeviceNotFoundException(
                        uuid.toString(),
                        deviceRequest.deviceName(),
                        deviceRequest.deviceType(),
                        "Device not found"
                ));
        update.setDeviceName(deviceRequest.deviceName());
        update.setDeviceType(deviceRequest.deviceType());
        update.setIpAdress(deviceRequest.ipAddress());
        update.setStatus(deviceRequest.status());
        Device saved=repository.save(update);
        return mapper.toResponse(saved);
    }

    @Override
    public DeviceResponse deleteDevice(UUID uuid) {
        Device deleteDevice=repository
                .findById(uuid)
                .orElseThrow(()-> new DeviceNotFoundException(
                        uuid.toString(),
                        null,
                        null,
                        "Device not found!"));
        repository.delete(deleteDevice);
        return mapper.toResponse(deleteDevice);
    }
}
