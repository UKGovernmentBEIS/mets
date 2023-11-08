package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.fallback;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.fallback.FallbackApproachEmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AerFallbackReportableEmissionsValidator implements AerFallbackEmissionValidator {

    private final FallbackApproachEmissionsCalculationService fallbackApproachEmissionsCalculationService;

    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    @Override
    public List<AerViolation> validate(FallbackEmissions fallbackEmission,
                                       AerContainer aerContainer) {
        FallbackEmissionsCalculationParamsDTO fallbackEmissionsCalculationParamsDTO =
            EMISSION_CALCULATION_PARAMS_MAPPER.toFallbackEmissionsCalculationParamsDTO(fallbackEmission);

        FallbackEmissionsCalculationDTO calculatedEmissions =
            fallbackApproachEmissionsCalculationService.calculateEmissions(fallbackEmissionsCalculationParamsDTO);

        FallbackEmissionsCalculationDTO fallbackEmissionsCalculationDTO =
            EMISSION_CALCULATION_PARAMS_MAPPER.toFallbackEmissionsCalculationDTO(fallbackEmission);

        List<AerViolation> violations = new ArrayList<>();
        if (!calculatedEmissions.equals(fallbackEmissionsCalculationDTO)) {
            violations.add(new AerViolation(MeasurementOfCO2Emissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.MEASUREMENT_INCORRECT_TOTAL_EMISSIONS));
        }
        return violations;
    }
}
