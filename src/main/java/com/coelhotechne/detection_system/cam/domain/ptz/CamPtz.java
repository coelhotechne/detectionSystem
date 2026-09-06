package com.coelhotechne.detection_system.cam.domain.ptz;

import com.coelhotechne.detection_system.cam.domain.base.BaseCam;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ptz_cam")
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EntityListeners(AuditingEntityListener.class)
public class CamPtz extends BaseCam {
    private Float pan_right;
    private Float pan_left;
    private Float tilt_up;
    private Float tilt_down;
    private Float zoom_out;
    private Float zoom_in;
    private String presets;
    private Integer currentPreset;
    private String lastPtzCommand;
    private Boolean isMoving;
}
