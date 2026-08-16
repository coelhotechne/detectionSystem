package com.coelhotechne.detection_system.device.application;

import com.coelhotechne.detection_system.device.infrastructure.DeviceRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeviceTestService {
    @Mock
    private DeviceRepository repository;
    @InjectMocks
    private DeviceService service;
}
