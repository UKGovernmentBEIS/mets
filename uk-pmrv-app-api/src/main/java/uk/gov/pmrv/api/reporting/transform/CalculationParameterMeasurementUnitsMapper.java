package uk.gov.pmrv.api.reporting.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CalculationParameterMeasurementUnitsMapper {

    CalculationParameterMeasurementUnits toCalculationParameterMeasurementUnits(EmissionsCalculationParamsDTO emissionCalculationParams);
}
