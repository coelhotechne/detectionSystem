package com.coelhotechne.detection_system.cam.domain.homologation;

import com.coelhotechne.detection_system.cam.domain.homologation.enums.CamHomologationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@Getter
@NoArgsConstructor
public class CamHomologationRecord {
    @Enumerated(EnumType.STRING)
    @Column(name = "homologation_status")
    private CamHomologationStatus status = CamHomologationStatus.PENDING_TEST;
    @Column(name = "homologation_last_tested_at")
    private Instant lastTestedAt;
    @Column(name = "homologation_failure_reason")
    private String failureReason;
    @Column(name = "homologation_resolved_codec")
    private String resolvedCodec;
    @Column(name = "homologation_decided_by")
    private String decidedBy;
    @Column(name = "homologation_decided_at")
    private Instant decidedAt;
    @Column(name = "homologation_rejection_reason")
    private String rejectionReason;

    public void applyTestResult(CamConnectionTestOutcome outcome) {
        this.lastTestedAt = Instant.now();
        if (outcome.success()) {
            this.status = CamHomologationStatus.PENDING_APPROVAL;
            this.failureReason = null;
            this.resolvedCodec = outcome.resolvedCodec();
        } else {
            this.status = CamHomologationStatus.TEST_FAILED;
            this.failureReason = outcome.errorMessage();
            this.resolvedCodec = null;
        }
    }

    public void approve(String approvedBy) {
        requireStatus(CamHomologationStatus.PENDING_APPROVAL, "approved");
        this.status = CamHomologationStatus.APPROVED;
        this.decidedBy = approvedBy;
        this.decidedAt = Instant.now();
        this.rejectionReason = null;
    }

    public void reject(String rejectedBy, String reason) {
        requireStatus(CamHomologationStatus.PENDING_APPROVAL, "rejected");
        this.status = CamHomologationStatus.REJECTED;
        this.decidedBy = rejectedBy;
        this.decidedAt = Instant.now();
        this.rejectionReason = reason;
    }

    private void requireStatus(CamHomologationStatus required, String action) {
        if (this.status != required) {
            throw new IllegalStateException(
                    "It is only possible" + action + " stems from  " + required + "; current status: " + this.status);
        }
    }
}

