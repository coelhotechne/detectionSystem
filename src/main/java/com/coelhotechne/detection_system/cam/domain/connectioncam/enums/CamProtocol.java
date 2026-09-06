package com.coelhotechne.detection_system.cam.domain.connectioncam.enums;

public enum CamProtocol {
    NATIVE,           // hardware Coelho Techne, protocolo próprio (MQTT/WS interno)
    RTSP,             // maioria das câmeras de mercado baratas/genéricas
    ONVIF,            // câmeras IP "sérias" que seguem o padrão da indústria
    PROPRIETARY_SDK   // Hikvision, Dahua, Reolink... cada uma com SDK próprio
}
