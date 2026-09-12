package com.coelhotechne.detection_system.cam.api.dto.fixed;

import com.coelhotechne.detection_system.cam.domain.homologation.CamHomologationRecord;
import com.coelhotechne.detection_system.cam.domain.homologation.enums.CamHomologationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CamHomologationSummary(
        CamHomologationStatus status,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant lastTestedAt,

        String failureReason,
        String resolvedCodec,
        String decidedBy,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant decidedAt,

        String rejectionReason
) {
    public static CamHomologationSummary from(CamHomologationRecord record) {
        if (record == null) {
            return null;
        }
        return new CamHomologationSummary(
                record.getStatus(),
                record.getLastTestedAt(),
                record.getFailureReason(),
                record.getResolvedCodec(),
                record.getDecidedBy(),
                record.getDecidedAt(),
                record.getRejectionReason()
        );
    }
}
