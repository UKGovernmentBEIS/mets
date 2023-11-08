package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproachType;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#emissionsCorrect) == (#manuallyInternationalFlightsProvidedEmissions != null)}",
    message = "aviationAerVerificationData.corsia.verifierDetails.opinionStatement.emissionsCorrect.manuallyInternationalFlights")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#emissionsCorrect) == (#manuallyOffsettingFlightsProvidedEmissions != null)}",
    message = "aviationAerVerificationData.corsia.verifierDetails.opinionStatement.emissionsCorrect.manuallyOffsettingFlights")
public class AviationAerCorsiaOpinionStatement {

    @Builder.Default
    private Set<@NotNull AviationAerCorsiaFuelType> fuelTypes = new HashSet<>();

    @NotNull
    private AviationAerCorsiaMonitoringApproachType monitoringApproachType;

    @NotNull
    private Boolean emissionsCorrect;

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal manuallyInternationalFlightsProvidedEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal manuallyOffsettingFlightsProvidedEmissions;
}
