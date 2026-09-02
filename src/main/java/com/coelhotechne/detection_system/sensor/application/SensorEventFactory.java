package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.domain.sensorevent.SensorEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorEventFactory {
    private final ObjectMapper objectMapper;

    public SensorEvent create(
            SensorTopic topic,
            String payload
    ) {

        try {

            return switch (topic.eventType()) {

                case "detection" ->
                        createDetection(
                                topic.sensorId(),
                                payload
                        );

                case "status" ->
                        createStatus(
                                topic.sensorId(),
                                payload
                        );

                case "telemetry" ->
                        createTelemetry(
                                topic.sensorId(),
                                payload
                        );

                default ->
                        throw new UnsupportedSensorEventException(
                                topic.eventType()
                        );
            };

        } catch (JsonProcessingException e) {

            throw new SensorEventParseException(
                    "Invalid sensor event",
                    e
            );
        }
    }
}
