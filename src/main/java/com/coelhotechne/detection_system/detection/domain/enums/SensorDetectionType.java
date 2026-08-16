package com.coelhotechne.detection_system.detection.domain.enums;

public enum SensorDetectionType {
    GAS("Gas"),
    CO2("CO2"),
    LPG("LPG"),
    HIGH_HEAT("High Heat"),
    EXTREME_HEAT("Extreme Heat");

    private final String description;

    SensorDetectionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static SensorDetectionType fromDescription(String description) {
        for (SensorDetectionType type : SensorDetectionType.values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid detection type: " + description);
    }
}
