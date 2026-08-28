package com.coelhotechne.detection_system.cam.domain;

import com.coelhotechne.detection_system.batterySupply.domain.PowerSupply;
import com.coelhotechne.detection_system.cam.domain.enums.CamStatus;
import com.coelhotechne.detection_system.cam.domain.enums.ImageQuality;
import com.coelhotechne.detection_system.globalClass.entities.BaseEntity;
import com.coelhotechne.detection_system.installation.domain.Installation;
import com.coelhotechne.detection_system.zone.domain.Zone;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.InstantDeserializer;
import tools.jackson.databind.ext.javatime.ser.InstantSerializer;

import java.time.Instant;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "base_cam")
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class BaseCam extends BaseEntity {
    @Column()
    private String rtsp;
    @Enumerated(EnumType.STRING)
    private CamStatus camStatus;
    //resolution
    @Setter(AccessLevel.NONE)
    private Integer width;
    @Setter(AccessLevel.NONE)
    private Integer height;
    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    private ImageQuality imageQuality;
    @Column()
    private Float fps;
    @Column()
    private Integer bitrate;
    @Column
    private String compression;
    @Column(name = "frame_url")
    private String frameUrl;
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    @Column(name = "frame_timestamp")
    private Instant frameTimestamp;
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private Instant timestamp;
    @Embedded
    @EqualsAndHashCode.Exclude
    private PowerSupply powerSupply;
    @Embedded
    @EqualsAndHashCode.Exclude
    private Installation installation;
    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "zone_id")
    @EqualsAndHashCode.Exclude
    private Zone zone;
    public void setResolution(Integer width,Integer height){
        if (width==null||height==null){
            this.height=null;
            this.width=null;
            this.imageQuality=ImageQuality.UNKNOWN;
            return;
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width e height need to be > 0");
        }
        this.width=width;
        this.height=height;
        this.imageQuality=calculateImageQuality(width,height);
    }

    private ImageQuality calculateImageQuality(Integer width,Integer height){
        if (width <= 640 && height <= 480) {
            return ImageQuality.LOW;
        }
        if (width <= 1280 && height <= 720) {
            return ImageQuality.LOW_MEDIUM;
        }
        if (width <= 1920 && height <= 1080) {
            return ImageQuality.MEDIUM;
        }
        if (width <= 7680 && height <= 4320) {
            return ImageQuality.HIGH;
        }
        return ImageQuality.UNKNOWN;
    }
}
