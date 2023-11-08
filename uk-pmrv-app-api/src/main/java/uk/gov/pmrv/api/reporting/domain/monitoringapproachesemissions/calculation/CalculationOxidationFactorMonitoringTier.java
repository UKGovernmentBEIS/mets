package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorTier;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalculationOxidationFactorMonitoringTier extends CalculationParameterMonitoringTier {

    @NotNull
    private CalculationOxidationFactorTier tier;
}
