package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsiaProcessAnalysis {

    @NotBlank
    @Size(max = 10000)
    private String verificationActivities;

    @NotBlank
    @Size(max = 10000)
    private String strategicAnalysis;

    @NotBlank
    @Size(max = 10000)
    private String dataSampling;

    @NotBlank
    @Size(max = 10000)
    private String dataSamplingResults;

    @NotBlank
    @Size(max = 10000)
    private String emissionsMonitoringPlanCompliance;
}
