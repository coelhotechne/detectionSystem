package com.coelhotechne.detection_system.sensor.application.auth;

import com.coelhotechne.detection_system.sensor.domain.Sensor;
import com.coelhotechne.detection_system.sensor.exceptions.SensorAuthenticationException;
import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import com.coelhotechne.detection_system.sensor.security.SensorAccessKeyHasher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SensorAuthServiceImpl implements SensorAuthService {
    private final SensorRepository repository;
    private final SensorAccessKeyHasher hasher;

    @Override
    @Transactional
    public Optional<Sensor> authenticate(UUID sensorId, String rawAccessKey) {
        return repository.findById(sensorId)
                .filter(sensor -> hasher.matches(rawAccessKey, sensor.getAccessKey()))
                .map(sensor -> {
                    sensor.setLastCommunication(Instant.now());
                    return repository.save(sensor);
                });
    }

    @Override
    public void requireValidAccessKey(UUID sensorId, String rawAccessKey) {
        boolean valid = repository.findById(sensorId)
                .map(sensor -> hasher.matches(rawAccessKey, sensor.getAccessKey()))
                .orElse(false);
        if (!valid) {
            throw new SensorAuthenticationException(sensorId.toString(),
                    "Access key invalid or Sensor does not exist");
        }
    }
}
