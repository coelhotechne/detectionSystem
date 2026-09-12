package com.coelhotechne.detection_system.cam.application.fixed;

import com.coelhotechne.detection_system.cam.api.dto.fixed.CamFixedMapper;
import com.coelhotechne.detection_system.cam.api.dto.fixed.CamFixedRequest;
import com.coelhotechne.detection_system.cam.api.dto.fixed.CamFixedResponse;
import com.coelhotechne.detection_system.cam.domain.fixed.CamFixed;
import com.coelhotechne.detection_system.cam.exceptions.CamNotFoundException;
import com.coelhotechne.detection_system.cam.exceptions.CamConcurrentModificationException;
import com.coelhotechne.detection_system.cam.infrastructure.CamFixedRepository;
import com.coelhotechne.detection_system.location.application.LocationService;
import com.coelhotechne.detection_system.location.domain.Location;
import com.coelhotechne.detection_system.zone.application.ZoneService;
import com.coelhotechne.detection_system.zone.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Log4j2
public class CamFixedServiceImpl implements CamFixedService{
    private final CamFixedRepository repository;
    private final CamFixedMapper mapper;
    private final ZoneService zoneService;
    private final LocationService locationService;

    @Override
    public CamFixed requireCam(UUID camId) {
        if (camId==null){
            throw new IllegalArgumentException("Cam id cannot be null");
        }
        return repository.findById(camId)
                .orElseThrow(() -> new CamNotFoundException(camId.toString(),"Cam not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CamFixedResponse> findCamList() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CamFixedResponse> findCamList(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CamFixedResponse findCamId(UUID uuid) {
        CamFixed cam = requireCam(uuid);
        return mapper.toResponse(cam);
    }

    @Override
    @Transactional
    public CamFixedResponse createCam(CamFixedRequest camFixedRequest) {
        Zone zoneEntity = camFixedRequest.zoneId() != null
                ? zoneService.requireZone(camFixedRequest.zoneId())
                : null;
        Location locationEntity = camFixedRequest.coverageLocationId() != null
                ? locationService.requireLocation(camFixedRequest.coverageLocationId())
                : null;

        CamFixed entity = mapper.toEntity(camFixedRequest);
        entity.setZone(zoneEntity);
        entity.setCoverageLocation(locationEntity);

        if (entity.getBaselineFrameUrl() != null) {
            entity.setBaselineCapturedAt(Instant.now());
        }

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public CamFixedResponse updateCam(UUID uuid, CamFixedRequest camFixedRequest) {
        CamFixed camFixed = requireCam(uuid);
        Zone zoneEntity = camFixedRequest.zoneId() != null
                ? zoneService.requireZone(camFixedRequest.zoneId())
                : null;
        Location locationEntity = camFixedRequest.coverageLocationId() != null
                ? locationService.requireLocation(camFixedRequest.coverageLocationId())
                : null;

        camFixed.setCamModel(camFixedRequest.camModel());
        camFixed.setFirmwareVersion(camFixedRequest.firmwareVersion());
        camFixed.setSerialNumber(camFixedRequest.serialNumber());
        camFixed.setMacAddress(camFixedRequest.macAddress());
        camFixed.setIpAddress(camFixedRequest.ipAddress());
        camFixed.setRtsp(camFixedRequest.rtsp());

        camFixed.setFps(camFixedRequest.fps());
        camFixed.setBitrate(camFixedRequest.bitrate());
        camFixed.setCompression(camFixedRequest.compression());
        camFixed.setFrameUrl(camFixedRequest.frameUrl());

        camFixed.setZone(zoneEntity);
        camFixed.setCoverageLocation(locationEntity);

        camFixed.setMountingAzimuthDeg(camFixedRequest.mountingAzimuthDeg());
        camFixed.setMountingElevationDeg(camFixedRequest.mountingElevationDeg());
        camFixed.setHorizontalFovDeg(camFixedRequest.horizontalFovDeg());
        camFixed.setVerticalFovDeg(camFixedRequest.verticalFovDeg());
        camFixed.setDetectionRangeMeters(camFixedRequest.detectionRangeMeters());
        camFixed.setDetectionMaskJson(camFixedRequest.detectionMaskJson());
        camFixed.setHomographyMatrixJson(camFixedRequest.homographyMatrixJson());

        // conferencia de  Base Line URL
        String previousBaselineFrameUrl = camFixed.getBaselineFrameUrl();

        camFixed.setBaselineFrameUrl(camFixedRequest.baselineFrameUrl());
        if (camFixed.getBaselineFrameUrl()!=null && !Objects.equals(previousBaselineFrameUrl, camFixed.getBaselineFrameUrl())){
            camFixed.setBaselineCapturedAt(Instant.now());
        }

        camFixed.setCrossingLineJson(camFixedRequest.crossingLineJson());
        camFixed.setDetectionConfidenceThreshold(camFixedRequest.detectionConfidenceThreshold());

        CamFixed saved = saveOrConflict(camFixed);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CamFixedResponse deleteCam(UUID uuid) {
        CamFixed camFixed = requireCam(uuid);
        CamFixedResponse response = mapper.toResponse(camFixed);
        repository.delete(camFixed);
        log.info("Cam with ID {}, deleted successfully",uuid);
        return response;
    }

    private CamFixed saveOrConflict(CamFixed camFixed) {
        try {
            return repository.save(camFixed);
        } catch (OptimisticLockingFailureException e) {
            throw new CamConcurrentModificationException(
                    camFixed.getUuid() != null ? camFixed.getUuid().toString() : null,
                    "Cam was modified concurrently."
            );
        }
    }
}
