package com.coelhotechne.detection_system.connection.domain.enums;

import lombok.Getter;

@Getter
public enum ApplicationProtocol {

    // API / Web
    HTTP,
    HTTPS,
    WEBSOCKET,

    // IoT / Messaging
    MQTT,
    MQTT_SN,
    COAP,
    LWM2M,
    AMQP,
    DDS,

    // Camera / Video
    RTSP,
    RTP,
    RTCP,
    ONVIF,
    WEBRTC,
    SRT,

    // Industrial
    OPC_UA,
    MODBUS_TCP,
    BACNET_IP,
    DNP3,

    // Network Management
    SNMP,
    DNS,
    DHCP,
    NTP
}
