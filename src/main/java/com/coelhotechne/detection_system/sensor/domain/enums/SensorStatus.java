package com.coelhotechne.detection_system.sensor.domain.enums;

public enum SensorStatus {
    /** Sensor sem comunicação ou desconectado. */
    DISCONNECTED(0, "Disconnected"),

    /** Em inicialização, boot ou calibração automática. */
    INITIALIZING(1, "Initializing"),

    /** Operando perfeitamente e enviando dados válidos. */
    OK(2, "Normal operation"),

    /** Funcionando, mas operando fora dos limites ideais (Alerta). */
    OUT_OF_SPECIFICATION(3, "Out of specification"),

    /** Funcionando, mas necessita de intervenção/limpeza em breve. */
    MAINTENANCE_REQUIRED(4, "Maintenance required"),

    /** Falha crítica de hardware ou leitura corrompida. */
    FAULT(5, "Hardware fault");

    private final Integer code;
    private final String description;

    SensorStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == INITIALIZING || this == OK || this == OUT_OF_SPECIFICATION || this == MAINTENANCE_REQUIRED;
    }

    /**
     * Recupera o Enum a partir do código numérico (útil para APIs e Bancos de Dados).
     */
    public static SensorStatus fromCode(int code) {
        for (SensorStatus state : SensorStatus.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        throw new IllegalArgumentException("Status code from sensor invalid, code: " + code);
    }
}
