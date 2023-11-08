package uk.gov.pmrv.api.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.CarbonContentMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculationParameterMeasurementUnits {

    private ActivityDataMeasurementUnit activityDataMeasurementUnit;

    private EmissionFactorMeasurementUnit efMeasurementUnit;

    private NCVMeasurementUnit ncvMeasurementUnit;

    private CarbonContentMeasurementUnit carbonContentMeasurementUnit;
}
