package uk.gov.pmrv.api.reporting.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.Year;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SpELExpression(expression = "{#reportingYear != null && T(java.lang.Integer).parseInt(#reportingYear) >= 1}",
    message = "reporting.n2o.emissionsCalculation.reportingYear.invalid")
public class MeasurementN2OEmissionsCalculationParamsDTO extends MeasurementEmissionsCalculationParamsDTO {

    @NotNull
    private Year reportingYear;
}
