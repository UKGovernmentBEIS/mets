package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#additionalChangesNotCovered) == (#additionalChangesNotCoveredDetails != null)}", message = "aerVerificationData.opinionStatement.additionalChangesNotCoveredDetails")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#operatorEmissionsAcceptable) == (#monitoringApproachTypeEmissions != null)}", message = "aerVerificationData.opinionStatement.monitoringApproachTypeEmissions")
public class OpinionStatement {

    @Builder.Default
    private Set<RegulatedActivityType> regulatedActivities = new HashSet<>();

    @Builder.Default
    private Set<@NotEmpty String> combustionSources = new HashSet<>();
    
    @Builder.Default
    private Set<@NotEmpty String> processSources = new HashSet<>();

    @NotEmpty
    @Size(max = 10000)
    private String monitoringApproachDescription;

    @NotEmpty
    @Size(max = 10000)
    private String emissionFactorsDescription;

    @NotNull
    private Boolean operatorEmissionsAcceptable;

    @Valid
    private MonitoringApproachTypeEmissions monitoringApproachTypeEmissions;

    @NotNull
    private Boolean additionalChangesNotCovered;

    private String additionalChangesNotCoveredDetails;

    @NotNull
    @Valid
    private SiteVisit siteVisit;
}
