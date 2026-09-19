package com.coelhotechne.detection_system.sensor.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Vizualização:
 *
 * <pre>
 * sensor.mqtt.broker-url=ssl://broker.interno:8883
 * sensor.mqtt.client-id=detection-system
 * sensor.mqtt.username=detection-system
 * sensor.mqtt.password=${MQTT_PASSWORD}
 * sensor.mqtt.clean-session=false
 * sensor.mqtt.persistence-dir=/var/lib/detection-system/mqtt
 * sensor.mqtt.unique-client-id-suffix=false
 * sensor.mqtt.ingestion-threads=4
 * sensor.mqtt.ingestion-queue-capacity=5000
 * sensor.mqtt.reconnect-delay=5s
 * </pre>
 */
@ConfigurationProperties(prefix = "sensor.mqtt")
public record SensorMqttProperties (
    String brokerUrl,
    String clientId,
    String username,
    String password,
    Boolean cleanSession,
    String persistenceDir,
    Boolean uniqueClientIdSuffix,
    Integer ingestionThreads,
    Integer ingestionQueueCapacity,
    Duration reconnectDelay
) {
    public SensorMqttProperties {
            cleanSession = cleanSession != null ? cleanSession : Boolean.FALSE;
            uniqueClientIdSuffix = uniqueClientIdSuffix != null ? uniqueClientIdSuffix : Boolean.FALSE;
            persistenceDir = persistenceDir != null ? persistenceDir : "./mqtt-persistence";
            ingestionThreads = ingestionThreads != null ? ingestionThreads : 4;
            ingestionQueueCapacity = ingestionQueueCapacity != null ? ingestionQueueCapacity : 5_000;
            reconnectDelay = reconnectDelay != null ? reconnectDelay : Duration.ofSeconds(5);
        }

}
