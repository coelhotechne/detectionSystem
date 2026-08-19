package com.coelhotechne.detection_system.zone.infrastructure;

import com.coelhotechne.detection_system.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ZoneRepository extends JpaRepository<Zone, UUID> {
    Optional<Zone>findById(UUID uuid);
}
