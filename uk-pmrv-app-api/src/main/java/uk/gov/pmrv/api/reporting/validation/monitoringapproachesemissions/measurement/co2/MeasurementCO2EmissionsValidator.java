package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.co2;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementCO2EmissionsValidator implements AerMeasurementCO2EmissionPointEmissionValidator {

    private final MeasurementCO2EmissionsCalculationService measurementCO2EmissionsCalculationService;

    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    @Override
    public List<AerViolation> validate(MeasurementEmissionPointEmission measurementEmissionPointEmission,
                                       AerContainer aerContainer) {
        MeasurementEmissionsCalculationParamsDTO measurementEmissionsCalculationParamsDTO =
            EMISSION_CALCULATION_PARAMS_MAPPER.toMeasurementCO2EmissionsCalculationParamsDTO(
            (MeasurementCO2EmissionPointEmission) measurementEmissionPointEmission);

        MeasurementEmissionsCalculationDTO calculatedEmissions =
            measurementCO2EmissionsCalculationService.calculateEmissions(
            measurementEmissionsCalculationParamsDTO);

        MeasurementEmissionsCalculationDTO measurementCO2EmissionsCalculationDTO =
            EMISSION_CALCULATION_PARAMS_MAPPER.toMeasurementEmissionsCalculationDTO(
            (MeasurementCO2EmissionPointEmission) measurementEmissionPointEmission);

        List<AerViolation> violations = new ArrayList<>();
        if (!calculatedEmissions.equals(measurementCO2EmissionsCalculationDTO)) {
            violations.add(new AerViolation(MeasurementOfCO2Emissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.MEASUREMENT_INCORRECT_TOTAL_EMISSIONS));
        }
        return violations;
    }
}
