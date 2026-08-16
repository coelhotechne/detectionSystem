package com.coelhotechne.detection_system.device.infrastructure;

import com.coelhotechne.detection_system.device.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findById(UUID uuid);
}
