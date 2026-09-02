package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.domain.sensorevent.SensorEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorEventHandler {
    private final ObjectMapper objectMapper;
    private final SensorService sensorService;

    public void handle(String topic, String payload) {

        SensorEvent event = parse(topic, payload);

        sensorService.process(event);
    }

    private SensorEvent parse(
            String topic,
            String payload
    ) {

        try {

            return objectMapper.readValue(
                    payload,
                    SensorEvent.class
            );

        } catch (JsonProcessingException e) {

            throw new SensorEventParseException(
                    "Unable to parse sensor event",
                    e
            );
        }
    }
}
