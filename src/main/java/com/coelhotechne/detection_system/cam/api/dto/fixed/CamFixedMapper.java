package com.coelhotechne.detection_system.cam.api.dto.fixed;

import com.coelhotechne.detection_system.cam.domain.fixed.CamFixed;
import com.coelhotechne.detection_system.globalClass.mapper.GenericMapper;
import com.coelhotechne.detection_system.location.domain.Location;
import com.coelhotechne.detection_system.location.infrastructure.LocationRepository;
import com.coelhotechne.detection_system.zone.domain.Zone;
import com.coelhotechne.detection_system.zone.infrastructure.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CamFixedMapper implements GenericMapper<CamFixed, CamFixedResponse, CamFixedRequest> {

    @Override
    public CamFixed toEntity(CamFixedRequest request) {
        CamFixed cam = new CamFixed();

        cam.setCamModel(request.camModel());
        cam.setFirmwareVersion(request.firmwareVersion());
        cam.setSerialNumber(request.serialNumber());
        cam.setMacAddress(request.macAddress());
        cam.setIpAddress(request.ipAddress());
        cam.setRtsp(request.rtsp());

        cam.setFps(request.fps());
        cam.setBitrate(request.bitrate());
        cam.setCompression(request.compression());
        cam.setFrameUrl(request.frameUrl());

        cam.setMountingAzimuthDeg(request.mountingAzimuthDeg());
        cam.setMountingElevationDeg(request.mountingElevationDeg());

        cam.setHorizontalFovDeg(request.horizontalFovDeg());
        cam.setVerticalFovDeg(request.verticalFovDeg());

        cam.setDetectionRangeMeters(request.detectionRangeMeters());
        cam.setDetectionMaskJson(request.detectionMaskJson());
        cam.setHomographyMatrixJson(request.homographyMatrixJson());

        cam.setBaselineFrameUrl(request.baselineFrameUrl());
        cam.setCrossingLineJson(request.crossingLineJson());
        cam.setDetectionConfidenceThreshold(request.detectionConfidenceThreshold());

        return cam;
    }

    @Override
    public CamFixedResponse toResponse(CamFixed entity) {
        return new CamFixedResponse(

                // BaseEntity
                entity.getUuid(),
                entity.getVersion(),
                entity.getCreatedBy(),
                entity.getLastModifiedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),

                // BaseCam
                entity.getCamModel(),
                entity.getFirmwareVersion(),
                entity.getSerialNumber(),
                entity.getMacAddress(),
                entity.getIpAddress(),
                entity.getRtsp(),

                entity.getCamStatus(),

                entity.getWidth(),
                entity.getHeight(),
                entity.getImageQuality(),

                entity.getFps(),
                entity.getBitrate(),
                entity.getCompression(),

                entity.getFrameUrl(),

                entity.getFrameTimestamp(),
                entity.getTimestamp(),

                entity.getPowerSupply(),
                entity.getInstallation(),

                entity.getZone() != null ? entity.getZone().getUuid() : null,

                CamConnectionSummary.from(entity.getCamConnection()),

                entity.getAccessKey() != null,

                entity.getLastCommunication(),

                CamHomologationSummary.from(entity.getHomologation()),

                // CamFixed
                entity.getCoverageLocation() != null ? entity.getCoverageLocation().getUuid() : null,

                entity.getMountingAzimuthDeg(),
                entity.getMountingElevationDeg(),

                entity.getHorizontalFovDeg(),
                entity.getVerticalFovDeg(),

                entity.getDetectionRangeMeters(),

                entity.getDetectionMaskJson(),

                entity.getHomographyMatrixJson(),

                entity.getBaselineFrameUrl(),

                entity.getBaselineCapturedAt(),

                entity.getCrossingLineJson(),

                entity.getDetectionConfidenceThreshold()
        );
    }
}