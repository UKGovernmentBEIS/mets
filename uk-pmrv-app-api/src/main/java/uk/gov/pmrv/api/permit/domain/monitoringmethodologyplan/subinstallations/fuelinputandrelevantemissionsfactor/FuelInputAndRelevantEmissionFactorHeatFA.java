package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#exists and #wasteGasesInput!=null and #hierarchicalOrder != null and #methodologyAppliedDescription != null and #methodologyAppliedDescription?.trim()?.length() > 0)" +
                " or (#exists==false and #wasteGasesInput==null and #hierarchicalOrder == null and (#methodologyAppliedDescription == null or #methodologyAppliedDescription.trim().length() == 0))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.fuelinputandrelevantemissionfactorFA.invalid_heat_fiaref"
)
public class FuelInputAndRelevantEmissionFactorHeatFA extends FuelInputAndRelevantEmissionFactorFA{

    @NotNull
    boolean exists;

}
