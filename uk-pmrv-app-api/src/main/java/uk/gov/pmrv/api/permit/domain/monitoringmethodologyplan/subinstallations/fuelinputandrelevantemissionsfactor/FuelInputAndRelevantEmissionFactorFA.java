package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
    @SpELExpression(
            expression = "{(#wasteGasesInput!=null && #hierarchicalOrder != null && #methodologyAppliedDescription != null " +
                    "&& #methodologyAppliedDescription?.trim()?.length() > 0 && #dataSources.size()>0) or (#wasteGasesInput==null " +
                    "&& #hierarchicalOrder == null && (#methodologyAppliedDescription == null or #methodologyAppliedDescription.trim().length() == 0))}",
            message = "permit.monitoringmethodologyplans.digitized.subinstallation.fuelinputandrelevantemissionfactorFA.invalid_fiaref"
    )
public class FuelInputAndRelevantEmissionFactorFA extends FuelInputAndRelevantEmissionFactor {

    Boolean wasteGasesInput;

    @Builder.Default
    @Size(max = 6)
    private List<@Valid @NotNull FuelInputDataSourceFA> dataSources = new ArrayList<>();

}
