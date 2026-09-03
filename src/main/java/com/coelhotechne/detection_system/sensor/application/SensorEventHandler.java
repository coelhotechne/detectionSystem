package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.event.SensorEvent;
import com.coelhotechne.detection_system.sensor.event.SensorEventProcessor;
import com.coelhotechne.detection_system.sensor.exceptions.SensorNotFoundException;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import com.coelhotechne.detection_system.sensor.mqtt.RawSensorTopic;
import com.coelhotechne.detection_system.sensor.mqtt.SensorTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SensorEventHandler {

    private final SensorEventFactory eventFactory;
    private final SensorRepository sensorRepository;
    private final SensorEventProcessor eventProcessor;

    public void handle(String rawTopic, String payload) {
        RawSensorTopic raw = RawSensorTopic.parse(rawTopic);

        UUID sensorId = sensorRepository.findByNameAndZoneName(raw.sensorName(), raw.zoneName())
                .map(Sensor::getUuid)
                .orElseThrow(() -> new SensorNotFoundException(raw.sensorName(),
                        "Sensor '%s' not found in zone '%s'".formatted(raw.sensorName(), raw.zoneName())));

        SensorTopic topic = new SensorTopic(sensorId, raw.eventType());
        SensorEvent event = eventFactory.create(topic, payload);
        eventProcessor.process(event);
    }
}
