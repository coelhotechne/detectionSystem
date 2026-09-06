package com.coelhotechne.detection_system.cam.infrastructure;

import com.coelhotechne.detection_system.cam.domain.ptz.CamPtz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CamPtzRepository extends JpaRepository<CamPtz, UUID> {
}
