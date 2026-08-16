package com.coelhotechne.detection_system.detection.domain.enums;

import lombok.Getter;

@Getter
public enum CamDetectionType {
    MOVING_ANIMAL("Moving Animal"),
    OBJECT("Object"),
    MOVING_OBJECT("Moving Object"),
    ANIMAL("Animal");

    private final String description;

    CamDetectionType(String description) {
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
