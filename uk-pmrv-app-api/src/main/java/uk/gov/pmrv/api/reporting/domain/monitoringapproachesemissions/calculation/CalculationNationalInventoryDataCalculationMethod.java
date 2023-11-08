package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalculationNationalInventoryDataCalculationMethod extends CalculationParameterCalculationMethod {

    @NotBlank
    private String mainActivitySector;

    @NotBlank
    private String fuel;

    @Valid
    @NotNull
    private CalculationInventoryEmissionCalculationParamValues emissionCalculationParamValues;
}
