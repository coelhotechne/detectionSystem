package com.coelhotechne.detection_system.connection.domain.enums;

import lombok.Getter;

@Getter
public enum LinkType {

    ETHERNET(
            ConnectionType.WIRED,
            true
    ),

    WIFI(
            ConnectionType.WIRELESS,
            true
    ),

    BLUETOOTH_LE(
            ConnectionType.WIRELESS,
            false
    ),

    ZIGBEE(
            ConnectionType.WIRELESS,
            false
    ),

    THREAD(
            ConnectionType.WIRELESS,
            false
    ),

    LORA(
            ConnectionType.WIRELESS,
            false
    ),

    CELLULAR_4G(
            ConnectionType.WIRELESS,
            true
    ),

    CELLULAR_5G(
            ConnectionType.WIRELESS,
            true
    ),

    FIBER(
            ConnectionType.WIRED,
            true
    );

    private final ConnectionType connectionType;
    private final boolean ipCapable;

    LinkType(
            ConnectionType connectionType,
            boolean ipCapable
    ) {
        this.connectionType = connectionType;
        this.ipCapable = ipCapable;
    }
}
