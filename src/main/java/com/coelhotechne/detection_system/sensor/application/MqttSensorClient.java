package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.api.dto.SensorTelemetryPayload;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Log4j2
@RequiredArgsConstructor
public class MqttSensorClient {
        @Value("${mqtt.broker.url}")
        private String broker;
        @Value("${mqtt.client.id}")
        private String clientId;
        private MqttClient client;

        private final SensorService sensorService;
        private final ObjectMapper objectMapper;

        @PostConstruct
        public void connect() throws Exception{
        client = new MqttClient(broker,clientId,new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                log.error("Mqtt connection error: {}",throwable);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String payload = new String(mqttMessage.getPayload());
                handlerSensorEvent(topic,payload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

            client.connect(options);
            client.subscribe("home/+/+/status", 1);
        }

        private void handlerSensorEvent(String topic, String payload){
            String[] partes = topic.split("/");
            String zone = partes[1];
            String sensorId = partes[2];
            try {
                SensorTelemetryPayload telemetry = objectMapper.readValue(payload, SensorTelemetryPayload.class);
                sensorService.applyTelemetry(zone, sensorId, telemetry,true);
                if (telemetry==null||telemetry.equals(0)){
                    sensorService.applyTelemetry(zone, sensorId, telemetry,false);
                }
            } catch (Exception e) {
                log.error("Sensor event processing failed:  {}/{}: {}", zone, sensorId, e.getMessage());
            }
        }
    public void publishCommand(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(topic, message);
    }

    @PreDestroy
    public void disconnect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }
}
