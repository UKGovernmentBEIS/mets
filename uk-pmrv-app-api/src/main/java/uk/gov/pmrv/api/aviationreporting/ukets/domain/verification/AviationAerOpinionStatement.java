package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#additionalChangesNotCovered) == (#additionalChangesNotCoveredDetails != null)}", message = "aviationAerVerificationData.opinionStatement.additionalChangesNotCovered.details")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#emissionsCorrect) == (#manuallyProvidedEmissions != null)}", message = "aviationAerVerificationData.opinionStatement.emissionsCorrect.manuallyProvidedEmissions")
public class AviationAerOpinionStatement {

    @Builder.Default
    private Set<@NotNull AviationAerUkEtsFuelType> fuelTypes = new HashSet<>();

    @NotNull
    private EmissionsMonitoringApproachType monitoringApproachType;

    @NotNull
    private Boolean emissionsCorrect;

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal manuallyProvidedEmissions;

    @NotNull
    private Boolean additionalChangesNotCovered;

    private String additionalChangesNotCoveredDetails;

    @NotNull
    @Valid
    private AviationAerSiteVisit siteVisit;


}
