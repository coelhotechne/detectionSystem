package com.coelhotechne.detection_system.sensor.domain;

import com.coelhotechne.detection_system.batterysupply.domain.PowerSupply;
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
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sensor")
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Sensor extends BaseEntity {
    @Column(nullable = false,name = "nome",length = 15)
    private String name;
    @Column(name = "sensor_status")
    private Boolean sensorStatus;
    @Column(name = "activation_time",nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime activationTime;
    @Column(name = "memory_used", precision = 15, scale = 2)
    private BigDecimal memoryUsed;
    @Column(name = "data_transfer_value", precision = 15, scale = 2)
    private BigDecimal dataTransferValue;
    @Column(name = "data_description",nullable = false)
    private String dataDescription;
    @Embedded
    @EqualsAndHashCode.Exclude
    private Installation installation;
    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "zone_id")
    @EqualsAndHashCode.Exclude
    private Zone zone;
    @Embedded
    @EqualsAndHashCode.Exclude
    private PowerSupply powerSupply;
}
