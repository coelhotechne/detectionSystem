package com.coelhotechne.detection_system.cam.domain.fixed;

import com.coelhotechne.detection_system.cam.domain.base.BaseCam;
import com.coelhotechne.detection_system.location.domain.Location;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixed_cam")
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EntityListeners(AuditingEntityListener.class)
public class CamFixed extends BaseCam {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "location_id")
    @EqualsAndHashCode.Exclude
    //lugar onde a câmera cobre, não onde foi instalado mas para onde aponta
    private Location coverageLocation;
    // ---- Geometria de mira (estatica por definicao) ----
    @Column(name = "mounting_azimuth_deg")
    private Float mountingAzimuthDeg;
    @Column(name = "mounting_elevation_deg")
    private Float mountingElevationDeg;
    // ---- Campo de visao da lente ----
    @Column(name = "horizontal_fov_deg")
    private Float horizontalFovDeg;
    @Column(name = "vertical_fov_deg")
    private Float verticalFovDeg;
    // ---- Alcance de deteccao configurado ----
    @Column(name = "detection_range_meters")
    private Float detectionRangeMeters;
    // ---- Mascara/zona de interesse dentro do frame ----
    @Column(name = "detection_mask_json", columnDefinition = "TEXT")
    private String detectionMaskJson;
    // ---- Calibracao frame
    @Column(name = "homography_matrix_json", columnDefinition = "TEXT")
    private String homographyMatrixJson;


    //  ---- para treinamento de IA -----

    // Comparacao de cenario vazio e nao vazio com horario
    @Column(name = "baseline_frame_url")
    private String baselineFrameUrl;

    @Column(name = "baseline_captured_at")
    private Instant baselineCapturedAt;


    // ---- Linha de comparacao, caso cruzamento dela dispara um alerta ----
    @Column(name = "crossing_line_json", columnDefinition = "TEXT")
    private String crossingLineJson;
    // limiar de confianca, aumenta a precisao de deteccao
    @Column(name = "detection_confidence_threshold")
    private Float detectionConfidenceThreshold;
}
