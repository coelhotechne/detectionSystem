package com.coelhotechne.detection_system.sensor.application;

import com.coelhotechne.detection_system.sensor.infrastructure.SensorRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SensorTestService {
    @Mock
    private SensorRepository repository;
    @InjectMocks
    private SensorService service;
}
