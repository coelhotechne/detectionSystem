package com.coelhotechne.detection_system.cam.api.dto.fixed;

import com.coelhotechne.detection_system.cam.domain.fixed.CamFixed;
import com.coelhotechne.detection_system.location.domain.Location;
import com.coelhotechne.detection_system.zone.domain.Zone;
import jakarta.validation.constraints.NotBlank;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CamFixedRequest(

        // ---- BaseCam ----

        @NotBlank
        String camModel,
        @NotBlank
        String firmwareVersion,
        @NotBlank
        String serialNumber,
        @NotBlank
        String macAddress,
        @NotBlank
        String ipAddress,
        String rtsp,
        Float fps,
        Integer bitrate,
        String compression,
        String frameUrl,
        UUID zoneId,
        UUID coverageLocationId,

        // ---- Alimentação / instalação / conexão ----
        // Podem ser DTOs próprios posteriormente
        Object powerSupply,
        Object installation,
        Object camConnection,
        // ---- CamFixed ----

        Float mountingAzimuthDeg,
        Float mountingElevationDeg,
        Float horizontalFovDeg,
        Float verticalFovDeg,
        Float detectionRangeMeters,
        String detectionMaskJson,
        String homographyMatrixJson,
        String baselineFrameUrl,
        String crossingLineJson,
        Float detectionConfidenceThreshold

) {

    public static CamFixed toEntity(CamFixedRequest request, Zone zone, Location location) {

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

        cam.setZone(zone);
        cam.setCoverageLocation(location);

        cam.setMountingAzimuthDeg(request.mountingAzimuthDeg());
        cam.setMountingElevationDeg(request.mountingElevationDeg());

        cam.setHorizontalFovDeg(request.horizontalFovDeg());
        cam.setVerticalFovDeg(request.verticalFovDeg());

        cam.setDetectionRangeMeters(request.detectionRangeMeters());
        cam.setDetectionMaskJson(request.detectionMaskJson());
        cam.setHomographyMatrixJson(request.homographyMatrixJson());

        cam.setBaselineFrameUrl(request.baselineFrameUrl());
        cam.setCrossingLineJson(request.crossingLineJson());
        cam.setDetectionConfidenceThreshold(
                request.detectionConfidenceThreshold()
        );

        return cam;
    }
}
