package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFractionTier;

import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalculationBiomassFractionMonitoringTier extends CalculationParameterMonitoringTier {

    @NotNull
    private CalculationBiomassFractionTier tier;
}
