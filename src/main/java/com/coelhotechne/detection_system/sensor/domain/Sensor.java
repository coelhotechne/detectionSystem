package com.coelhotechne.detection_system.sensor.domain;

import com.coelhotechne.detection_system.batterysupply.domain.PowerSupply;
import com.coelhotechne.detection_system.globalClass.entities.BaseEntity;
import com.coelhotechne.detection_system.installation.domain.Installation;
import com.coelhotechne.detection_system.sensor.api.dto.SensorTelemetryPayload;
import com.coelhotechne.detection_system.sensor.domain.enums.SensorStatus;
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
import java.time.Instant;
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

    private static final BigDecimal DATA_TRANSFER_OUT_OF_SPEC = BigDecimal.valueOf(95);

    @Column(nullable = false,name = "nome",length = 15)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_status", nullable = false, length = 30)
    private SensorStatus status = SensorStatus.INITIALIZING;
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

    /**
     * Aplica uma leitura de diagnóstico recebida via telemetria MQTT e
     * recalcula o status a partir dela — o status NUNCA é aceito como input
     * direto (mesmo padrão já decidido pra PowerSupply.applyReading: "status
     * é calculado, não recebido"). Retorna o status anterior, pra quem chamou
     * decidir se quer emitir um SensorStatusEvent.
     */
    public SensorStatus applyDiagnostics(SensorTelemetryPayload payload, Instant now) {
        SensorStatus previous = this.status;

        this.memoryUsed = payload.memoryUsed();
        this.dataTransferValue = payload.dataTransferValue();
        this.dataDescription = payload.dataDescription();
        this.status = resolveStatus(payload);

        return previous;
    }

    private SensorStatus resolveStatus(SensorTelemetryPayload payload) {
        if (Boolean.FALSE.equals(payload.status())) {
            return SensorStatus.FAULT;
        }
        if (payload.dataTransferValue() != null
                && payload.dataTransferValue().compareTo(DATA_TRANSFER_OUT_OF_SPEC) > 0) {
            return SensorStatus.OUT_OF_SPECIFICATION;
        }
        return SensorStatus.OK;
    }

    /**
     * Chamado por um scanner de inatividade agendado — mesmo papel do
     * BatteryDisconnectionScanner já decidido pra PowerSupply, aplicado aqui
     * pra sensores que pararam de reportar dentro da janela esperada.
     */
    public SensorStatus markDisconnected() {
        SensorStatus previous = this.status;
        this.status = SensorStatus.DISCONNECTED;
        return previous;
    }
}
