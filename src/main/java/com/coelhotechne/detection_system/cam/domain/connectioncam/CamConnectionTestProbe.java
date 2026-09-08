package com.coelhotechne.detection_system.cam.domain.connectioncam;

import com.coelhotechne.detection_system.cam.domain.homologation.CamConnectionTestOutcome;

public interface CamConnectionTestProbe {
    CamConnectionTestOutcome test(CamConnectionProfile connection);
}
