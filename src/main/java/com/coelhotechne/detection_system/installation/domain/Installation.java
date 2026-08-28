package com.coelhotechne.detection_system.installation.domain;

import com.coelhotechne.detection_system.globalClass.entities.BaseEntity;
import com.coelhotechne.detection_system.installation.domain.enums.InstallationStatus;
import com.coelhotechne.detection_system.location.domain.Location;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Installation{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "installed_at")
    private LocalDateTime installedAt;
    @Column(name = "installed_by")
    private String installedBy;
    @Enumerated(EnumType.STRING)
    @Column(name = "installation_status")
    private InstallationStatus installationStatus;
    @Column(name = "installation_notes")
    private String installationNotes;
}
