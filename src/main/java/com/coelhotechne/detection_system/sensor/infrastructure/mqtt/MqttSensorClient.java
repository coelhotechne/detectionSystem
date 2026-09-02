package com.coelhotechne.detection_system.sensor.infrastructure.mqtt;

import com.coelhotechne.detection_system.sensor.api.dto.SensorTelemetryPayload;
import com.coelhotechne.detection_system.sensor.application.SensorService;
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
    private static final String STATUS_TOPIC_FILTER = "home/+/+/status";
    // home / {zone} / {sensor} / status
    private static final int STATUS_TOPIC_SEGMENTS = 4;

    @Value("${mqtt.broker.url}")
    private String broker;
    @Value("${mqtt.client.id}")
    private String clientId;

    private MqttClient client;

    private final SensorService sensorService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void connect() throws MqttException {
        client = new MqttClient(broker, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        // MqttCallbackExtended (não o MqttCallback simples) é o que dá acesso a
        // connectComplete(). Sem isso, setAutomaticReconnect(true) reconecta a
        // sessão TCP depois de uma queda de rede mas NUNCA reinscreve nos
        // tópicos — o Paho não faz isso sozinho — e o serviço parava de
        // receber telemetria pra sempre, em silêncio, até reiniciar a app.
        client.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                try {
                    client.subscribe(STATUS_TOPIC_FILTER, 1);
                    if (reconnect) {
                        log.warn("Mqtt reconectado em {} — reinscrito em {}", serverURI, STATUS_TOPIC_FILTER);
                    }
                } catch (MqttException e) {
                    log.error("Falha ao (re)inscrever em {} após connect: {}", STATUS_TOPIC_FILTER, e.getMessage());
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                log.error("Mqtt connection lost: {}", throwable.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                handlerSensorEvent(topic, new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // publishCommand não depende de confirmação assíncrona hoje.
            }
        });

        client.connect(options);
        // a inscrição inicial acontece em connectComplete(false, ...), chamado
        // automaticamente logo após o connect() ter sucesso — não precisa
        // repetir client.subscribe(...) aqui.
    }

    private void handlerSensorEvent(String topic, String payload) {
        String[] partes = topic.split("/");
        if (partes.length < STATUS_TOPIC_SEGMENTS) {
            log.warn("Tópico mqtt em formato inesperado, ignorando mensagem: {}", topic);
            return;
        }

        String zoneName = partes[1];
        String sensorName = partes[2];

        try {
            SensorTelemetryPayload telemetry = objectMapper.readValue(payload, SensorTelemetryPayload.class);
            // chamada única — antes o handler aplicava telemetria com status=true
            // incondicionalmente e depois, numa condição que nunca era
            // verdadeira (telemetry.equals(0) nunca bate pra um record), tentava
            // aplicar de novo com status=false. O campo payload.status() em si
            // nunca era lido. Agora o status é derivado dentro de
            // Sensor.applyDiagnostics(), a partir do payload completo, numa
            // única escrita.
            sensorService.applyTelemetry(zoneName, sensorName, telemetry);
        } catch (Exception e) {
            log.error("Sensor event processing failed: {}/{}: {}", zoneName, sensorName, e.getMessage());
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
