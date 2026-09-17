package com.coelhotechne.detection_system.sensor.domain;

import com.coelhotechne.detection_system.batterysupply.domain.PowerSupply;
import com.coelhotechne.detection_system.globalClass.entities.BaseEntity;
import com.coelhotechne.detection_system.installation.domain.Installation;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorNiche;
import com.coelhotechne.detection_system.sensor.domain.payload.SensorTelemetryPayload;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
import com.coelhotechne.detection_system.zone.domain.Zone;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLock;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.truncate;

@Setter
@Getter
@Entity
@DynamicUpdate
@NoArgsConstructor
@Table(name = "sensor",
        uniqueConstraints = @UniqueConstraint(
        name = "uk_sensor_nome_zone", columnNames = {"nome", "zone_id"}),
        indexes = {
                @Index(name = "idx_sensor_zone", columnList = "zone_id"),
                @Index(name = "idx_sensor_status", columnList = "sensor_status")
        }
)
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Sensor extends BaseEntity {
    private static final Pattern TOPIC_SAFE_NAME = Pattern.compile("^[A-Za-z0-9_-]{1,15}$");
    private static final int DATA_DESCRIPTION_MAX = 255;
    private static final BigDecimal DATA_TRANSFER_OUT_OF_SPEC = BigDecimal.valueOf(95);

    @Column(nullable = false,name = "nome",length = 15)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_niche",nullable = false)
    private SensorNiche sensorNiche;
    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_status", nullable = false, length = 30)
    private SensorStatus sensorStatus = SensorStatus.INITIALIZING;
    @Column(name = "activation_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime activationTime;
    @OptimisticLock(excluded = true)
    @Column(name = "memory_used", precision = 15, scale = 2)
    private BigDecimal memoryUsed;
    @OptimisticLock(excluded = true)
    @Column(name = "data_transfer_value", precision = 15, scale = 2)
    private BigDecimal dataTransferValue;
    @Column(name = "data_description", nullable = false, length = DATA_DESCRIPTION_MAX)
    private String dataDescription;
    @OptimisticLock(excluded = true)
    @Column(name = "last_communication")
    private Instant lastCommunication;
    @OptimisticLock(excluded = true)
    @Column(name = "last_reading_at")
    private Instant lastReadingAt;
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
    @JsonIgnore
    @Column(name = "access_key", unique = true, length = 64)
    private String accessKey;

    @PrePersist
    @PreUpdate
    private void validateInvariants(){
    if (name==null||!TOPIC_SAFE_NAME.matcher(name).matches()){
        throw new IllegalArgumentException("The sensor name is invalid for use with the MQTT topic:'"+name+"'");
    }
    }

    public record DiagnosticsOutcome(
            SensorStatus previousStatus,
            SensorStatus currentStatus,
            boolean persist
    ){
    public boolean statusChanged(){
        return previousStatus!=currentStatus;
    }
    }
    /**
     * Aplica uma leitura de diagnóstico recebida via telemetria MQTT e
     * recalcula o status a partir dela — o status NUNCA é aceito como input
     * direto (mesmo padrão já decidido pra PowerSupply.applyReading: "status
     * é calculado, não recebido"). Retorna o status anterior, pra quem chamou
     * decidir se quer emitir um SensorStatusEvent.
     */
    public DiagnosticsOutcome applyDiagnostics(SensorTelemetryPayload payload,
                                               Instant observedAt,
                                               SensorDiagnosticsThresholds thresholds,
                                               Duration heartbeatWriteInterval) {
        SensorStatus previous = this.sensorStatus;

        if (lastReadingAt!=null&& observedAt.isBefore(lastReadingAt)){
        return new DiagnosticsOutcome(previous,previous,false);
        }
        SensorStatus resolved =resolveStatus(payload,thresholds);
        boolean statusChanged= resolved!=previous;
        boolean heartbeatDue = lastReadingAt ==
                null || Duration.between(lastReadingAt, observedAt)
                .compareTo(heartbeatWriteInterval) >= 0;

        if (!statusChanged && !heartbeatDue) {
            return new DiagnosticsOutcome(previous, previous, false);
        }

        if (payload.memoryUsed() != null) {
            this.memoryUsed = payload.memoryUsed();
        }
        if (payload.dataTransferValue() != null) {
            this.dataTransferValue = payload.dataTransferValue();
        }
        if (payload.dataDescription() != null && !payload.dataDescription().isBlank()) {
            this.dataDescription = truncate(payload.dataDescription());
        }
        this.sensorStatus= resolved;
        this.lastReadingAt = observedAt;

        return new DiagnosticsOutcome(previous,resolved,true);
    }

    private SensorStatus resolveStatus(SensorTelemetryPayload payload,
                                       SensorDiagnosticsThresholds thresholds) {
        if (Boolean.FALSE.equals(payload.status())) {
            return SensorStatus.FAULT;
        }
        if (this.sensorStatus== SensorStatus.MAINTENANCE_REQUIRED) {
            return SensorStatus.MAINTENANCE_REQUIRED;
        }
        if (payload.dataTransferValue() != null
                && payload.dataTransferValue().compareTo(thresholds.dataTransferOutOfSpec()) > 0) {
            return SensorStatus.OUT_OF_SPECIFICATION;
        }
        return SensorStatus.OK;
    }

    public boolean applyReportedStatus(SensorStatus reported, Instant observedAt) {
        if (reported == null || !reported.isDeviceReportable()) {
            return false;
        }
        if (this.sensorStatus == SensorStatus.MAINTENANCE_REQUIRED
                && reported != SensorStatus.FAULT) {
            this.lastReadingAt = observedAt;
            return false;
        }
        this.lastReadingAt = observedAt;
        if (this.sensorStatus == reported) {
            return false;
        }
        this.sensorStatus = reported;
        return true;
    }

    public boolean requestMaintenance() {
        if (this.sensorStatus == SensorStatus.MAINTENANCE_REQUIRED) {
            return false;
        }
        this.sensorStatus = SensorStatus.MAINTENANCE_REQUIRED;
        return true;
    }

    public boolean clearMaintenance() {
        if (this.sensorStatus != SensorStatus.MAINTENANCE_REQUIRED) {
            return false;
        }
        this.sensorStatus= SensorStatus.INITIALIZING;
        return true;
    }

    public boolean detachZone() {
        if (this.zone == null) {
            return false;
        }
        this.zone = null;
        return true;
    }

    public void touchCommunication(Instant at) {
        this.lastCommunication = at;
    }

    private static String truncate(String value) {
        return value.length() <= DATA_DESCRIPTION_MAX
                ? value
                : value.substring(0, DATA_DESCRIPTION_MAX);
    }
    public boolean markDisconnected() {
        if (this.sensorStatus == SensorStatus.DISCONNECTED) {
            return false;
        }
        this.sensorStatus = SensorStatus.DISCONNECTED;
        return true;
    }

}
