package com.coelhotechne.detection_system.batterySupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PowerSupply {

    private static final BigDecimal LOW_TRESHOLD = new BigDecimal("30");
    private static final BigDecimal CRITICAL_TRESHOLD = new BigDecimal("15");

    @Enumerated(EnumType.STRING)
    @Column(name = "power_type")
    private PowerSupplyType type;
    @Enumerated(EnumType.STRING)
    @Column(name = "power_status")
    private PowerSupplyStatus status;
    @Column(name = "power_percentage",precision = 15, scale = 2)
    private BigDecimal percentage;
    @Column(name = "power_rechargeable")
    private Boolean rechargeable;
    @Column(name = "power_last_reading_at")
    private LocalDateTime lastReadingAt;

    public PowerSupply(PowerSupplyType type, Boolean rechargeable){
        this.type=type;
        this.rechargeable=rechargeable;
        this.status=isBatteryless()
                ? PowerSupplyStatus.NOT_APPLICABLE
                : PowerSupplyStatus.NORMAL;
    }

    public boolean isBatteryless(){
        return type==PowerSupplyType.WIRED
                || type==PowerSupplyType.POWER_GRID
                || type==PowerSupplyType.NONE;
    }

    public void applyReading(BigDecimal newPercentage, Boolean charging,LocalDateTime now){
        this.lastReadingAt = now;

        if (isBatteryless()){
            this.status = PowerSupplyStatus.NOT_APPLICABLE;
            this.percentage=null;
            return;
        }
        this.percentage=newPercentage;

        if (Boolean.TRUE.equals(charging)){
            this.status = PowerSupplyStatus.CHARGING;
            return;
        }

        if (newPercentage == null){
            return;
        }
        if (newPercentage.compareTo(CRITICAL_TRESHOLD)<=0){
            this.status=PowerSupplyStatus.CRITICAL;
        } else if (newPercentage.compareTo(LOW_TRESHOLD)<=0) {
            this.status=PowerSupplyStatus.LOW;
        }else {
            this.status=PowerSupplyStatus.NORMAL;
        }
    }
    public void markDisconnected(){
        if(!isBatteryless()){
            this.status = PowerSupplyStatus.DISCONNECTED;
        }
    }
}
