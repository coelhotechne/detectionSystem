package com.coelhotechne.detection_system.sensor.mqtt;

import com.coelhotechne.detection_system.sensor.application.event.SensorEventHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.*;

import static org.springframework.boot.util.LambdaSafe.callback;

@Service
@Log4j2
public class MqttSensorClient {

    private static final String[] EVENT_TOPICS = {
            "home/+/+/detection",
            "home/+/+/status",
            "home/+/+/telemetry"
    }; // status | telemetry | detection
    private static final int[] EVENT_QOS = {1, 1, 1};

    private final SensorMqttProperties properties;
    private final SensorEventHandler sensorEventHandler;
    private MqttClient client;
    private ThreadPoolExecutor ingestionExecutor;
    private ScheduledExecutorService scheduler;
    private volatile boolean shuttingDown;
    private volatile boolean subscriptionHealthy;
    private volatile long lastMessageAtMillis;

    @Value("${mqtt.broker.url}")
    private String broker;
    @Value("${mqtt.client.id}")
    private String clientId;

    public MqttSensorClient(SensorMqttProperties properties, SensorEventHandler sensorEventHandler) {
        this.properties = properties;
        this.sensorEventHandler = sensorEventHandler;
    }

    @PostConstruct
    public void start() throws MqttException {
        this.ingestionExecutor = new ThreadPoolExecutor(
                properties.ingestionThreads(), properties.ingestionThreads(),
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(properties.ingestionQueueCapacity()),
                daemonFactory("sensor-ingest"),
                new ThreadPoolExecutor.CallerRunsPolicy());

        this.scheduler = Executors.newSingleThreadScheduledExecutor(daemonFactory("sensor-mqtt-admin"));

        this.client = new MqttClient(properties.brokerUrl(), resolveClientId(), resolvePersistence());
        this.client.setCallback(callback());

        connectWithRetry();
    }

    private MqttClientPersistence resolvePersistence() {
        return Boolean.TRUE.equals(properties.cleanSession())
                ? new MemoryPersistence()
                : new MqttDefaultFilePersistence(properties.persistenceDir());
    }

    private String resolveClientId() {
        return Boolean.TRUE.equals(properties.uniqueClientIdSuffix())
                ? properties.clientId() + "-" + UUID.randomUUID().toString().substring(0, 8)
                : properties.clientId();
    }


    private void connectWithRetry() {
        if (shuttingDown) {
            return;
        }
        try {
            client.connect(connectOptions());
            log.info("MQTT broker connected in {}", properties.brokerUrl());
        } catch (MqttException e) {
            log.error("Failed to connect mqtt broker ({}), new try in: {}",
                    properties.brokerUrl(), properties.reconnectDelay(), e);
            scheduler.schedule(this::connectWithRetry,
                    properties.reconnectDelay().toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    private MqttConnectOptions connectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(Boolean.TRUE.equals(properties.cleanSession()));

        if (properties.username() != null && !properties.username().isBlank()) {
            options.setUserName(properties.username());
            options.setPassword(properties.password() == null
                    ? new char[0] : properties.password().toCharArray());
        } else {
            log.warn("MQTT connection without credentials — accepted only local broker " +
                    "development. In production, anyone on the network can trigger a false detection..");
        }
        return options;
    }

    private MqttCallbackExtended callback() {
        return new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    log.warn("Mqtt reconectado em {}", serverURI);
                }
                trySubscribe();
            }
            @Override
            public void connectionLost(Throwable throwable) {
                subscriptionHealthy = false;
                log.error("Mqtt connection lost", throwable);
            }
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                lastMessageAtMillis = System.currentTimeMillis();

                String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                ingestionExecutor.execute(() -> dispatch(topic, payload));
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        };
    }
    private void trySubscribe() {
        if (shuttingDown) {
            return;
        }
        try {
            client.subscribe(EVENT_TOPICS, EVENT_QOS);
            subscriptionHealthy = true;
            log.info("Written in {}", String.join(", ", EVENT_TOPICS));
        } catch (MqttException e) {
            subscriptionHealthy = false;
            log.error("Failed to write events topics — new try in {}",
                    properties.reconnectDelay(), e);
            scheduler.schedule(this::trySubscribe,
                    properties.reconnectDelay().toMillis(), TimeUnit.MILLISECONDS);
        }
    }


    private void dispatch(String topic, String payload) {
        try {
            sensorEventHandler.handle(topic, payload);
        } catch (Exception e) {
            log.error("Failed to process event topic: {}", topic, e);
        }
    }
    public void publishCommand(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        message.setQos(1);
        client.publish(topic, message);
    }
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public boolean isSubscriptionHealthy() {
        return subscriptionHealthy;
    }

    public long secondsSinceLastMessage() {
        return lastMessageAtMillis == 0
                ? -1
                : (System.currentTimeMillis() - lastMessageAtMillis) / 1000;
    }

    @PreDestroy
    public void stop() {
        shuttingDown = true;

        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        if (ingestionExecutor != null) {
            ingestionExecutor.shutdown();
            try {
                if (!ingestionExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
                    log.warn("Intake did not drain in 20s — {} discarded tasks",
                            ingestionExecutor.shutdownNow().size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            } catch (MqttException e) {
                log.warn("MQTT client close failed ", e);
            }
        }
    }

    private static ThreadFactory daemonFactory(String name) {
        return r -> {
            Thread t = new Thread(r, name);
            t.setDaemon(true);
            return t;
        };
    }
}

