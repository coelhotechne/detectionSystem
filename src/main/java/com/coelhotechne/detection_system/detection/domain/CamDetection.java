package com.coelhotechne.detection_system.detection.domain;

import com.coelhotechne.detection_system.cam.domain.CamFixed;
import com.coelhotechne.detection_system.cam.domain.CamPtz;
import com.coelhotechne.detection_system.detection.domain.enums.CamDetectionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cam_detection")
@AllArgsConstructor
public class CamDetection extends Detection{
    @Enumerated
    private CamDetectionType camDetectionType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cam_ptz_id")
    private CamPtz camPtz;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cam_fixed_id")
    private CamFixed camFixed;
}
