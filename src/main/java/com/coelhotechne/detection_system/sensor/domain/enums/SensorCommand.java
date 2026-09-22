package com.coelhotechne.detection_system.sensor.domain.enums;

public enum SensorCommand {
    /** Pede uma leitura imediata fora da cadência normal. */
    READ_NOW("READ_NOW"),
    /** Reinicia o dispositivo. */
    RESTART("RESTART"),
    /** Recalibra o sensor (volta a INITIALIZING até terminar). */
    CALIBRATE("CALIBRATE"),
    /** Autoteste de homologação — o requestId é concatenado pelo service. */
    SELF_TEST("SELF_TEST"),
    /** Silencia alarme local sem alterar o estado lógico no servidor. */
    MUTE("MUTE");
    
    private final String wireValue;

    SensorCommand(String wireValue) {
        this.wireValue = wireValue;
    }

    public String wireValue() {
        return wireValue;
    }
}