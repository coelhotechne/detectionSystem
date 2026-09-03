package com.coelhotechne.detection_system.sensor.event.application;

import com.coelhotechne.detection_system.sensor.application.SensorEventHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class MqttSensorClient {

    private static final String EVENT_TOPIC_FILTER = "home/+/+/+"; // status | telemetry | detection

    @Value("${mqtt.broker.url}")
    private String broker;
    @Value("${mqtt.client.id}")
    private String clientId;

    private MqttClient client;

    private final SensorEventHandler sensorEventHandler;

    @PostConstruct
    public void connect() throws MqttException {
        client = new MqttClient(broker, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        // MqttCallbackExtended (não MqttCallback) pra ganhar connectComplete():
        // com automaticReconnect(true) sozinho, o Paho reconecta a sessão TCP
        // depois de uma queda de rede mas nunca reinscreve nos tópicos — sem
        // isso o serviço parava de receber eventos em silêncio até reiniciar.
        client.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                try {
                    client.subscribe(EVENT_TOPIC_FILTER, 1);
                    if (reconnect) {
                        log.warn("Mqtt reconectado em {} — reinscrito em {}", serverURI, EVENT_TOPIC_FILTER);
                    }
                } catch (MqttException e) {
                    log.error("Falha ao (re)inscrever em {} após connect: {}", EVENT_TOPIC_FILTER, e.getMessage());
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                log.error("Mqtt connection lost: {}", throwable.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                try {
                    sensorEventHandler.handle(topic, new String(mqttMessage.getPayload()));
                } catch (Exception e) {
                    // handler já cobre tópico malformado, sensor não encontrado,
                    // payload inválido e conflito otimista — qualquer coisa que
                    // escapar daqui é logada, e a próxima mensagem MQTT segue
                    // sendo processada normalmente (uma mensagem ruim não derruba
                    // as seguintes).
                    log.error("Sensor event processing failed for topic {}: {}", topic, e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // publishCommand não depende de confirmação assíncrona hoje.
            }
        });

        client.connect(options);
        // inscrição inicial acontece dentro de connectComplete(false, ...),
        // chamado automaticamente logo após o connect() ter sucesso.
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

