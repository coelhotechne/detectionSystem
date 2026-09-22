package com.coelhotechne.detection_system.sensor.domain.enums;

public enum SensorNiche {

    SECURITY(0, "Security"),       // Seguranca e intrusao
    PRESENCE(1, "Presence"),       // Presenca e ocupacao
    HYDRAULIC(2, "Hydraulic"),     // Agua, vazamento, fluxo e nivel
    ENERGY(3, "Energy"),           // Energia eletrica
    GAS(4, "Gas"),                 // Gases adversos, diferenciado no service
    FIRE(5, "Fire"),               // Fumaca, chama e temperatura de incendio
    ENVIRONMENT(6, "Environment"), // Temperatura, umidade, pressao, ambiente
    AIR_QUALITY(7, "Air Quality"), // CO2, VOC, particulas e qualidade do ar
    LIGHTING(8, "Lighting"),       // Luz e luminosidade
    SOUND(9, "Sound"),             // Som e nivel sonoro
    WEATHER(10, "Weather"),        // Chuva, vento, radiacao solar, UV
    STRUCTURAL(11, "Structural"),  // Vibracao, deformacao, rachaduras
    POSITION(12, "Position"),      // Posicao, distancia, inclinacao
    WEIGHT(13, "Weight"),          // Peso e carga
    SOIL(14, "Soil"),              // Umidade, temperatura e propriedades do solo
    POOL(15, "Pool"),              // Piscina: pH, cloro, turbidez etc.
    VEHICLE(16, "Vehicle"),        // Veiculos e garagem
    ANIMAL(17, "Animal"),          // Animais domesticos
    HEALTH(18, "Health"),          // Monitoramento corporal e atividade
    IDENTIFICATION(19, "Identification"), // RFID, NFC, UWB etc.
    UNKNOWN(99, "Unknown");

    private final int code;
    private final String description;

    SensorNiche(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SensorNiche fromCode(int code) {
        for (SensorNiche niche : values()) {
            if (niche.getCode() == code) {
                return niche;
            }
        }

        throw new IllegalArgumentException(
                "Sensor niche code invalid: " + code
        );
    }
}