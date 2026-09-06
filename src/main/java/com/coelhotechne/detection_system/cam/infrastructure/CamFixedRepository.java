package com.coelhotechne.detection_system.cam.infrastructure;

import com.coelhotechne.detection_system.cam.domain.fixed.CamFixed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CamFixedRepository extends JpaRepository<CamFixed, UUID> {
}
