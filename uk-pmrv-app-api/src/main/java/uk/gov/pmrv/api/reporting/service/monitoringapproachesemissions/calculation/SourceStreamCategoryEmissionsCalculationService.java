package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;

import java.util.List;

public interface SourceStreamCategoryEmissionsCalculationService {

    EmissionsCalculationDTO calculateEmissions(EmissionsCalculationParamsDTO calculationParams);

    SourceStreamCategory getSourceStreamCategory();

    List<CalculationParameterMeasurementUnits> getValidMeasurementUnitsCombinations();
}
