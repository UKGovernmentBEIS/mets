package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalculationManualCalculationMethod extends CalculationParameterCalculationMethod {

    @Valid
    @NotNull
    private CalculationManualEmissionCalculationParamValues emissionCalculationParamValues;
}
