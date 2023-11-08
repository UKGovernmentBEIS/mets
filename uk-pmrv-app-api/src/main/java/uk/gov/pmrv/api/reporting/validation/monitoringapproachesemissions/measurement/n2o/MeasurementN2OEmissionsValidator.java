package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.n2o;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementN2OEmissionsValidator implements AerMeasurementN2OEmissionPointEmissionValidator {

    private final MeasurementN2OEmissionsCalculationService measurementN2OEmissionsCalculationService;

    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    @Override
    public List<AerViolation> validate(MeasurementEmissionPointEmission measurementEmissionPointEmission,
                                       AerContainer aerContainer) {
        MeasurementEmissionsCalculationParamsDTO measurementN2OEmissionsCalculationParamsDTO =
            EMISSION_CALCULATION_PARAMS_MAPPER.toMeasurementN2OEmissionsCalculationParamsDTO(
                (MeasurementN2OEmissionPointEmission) measurementEmissionPointEmission);

        MeasurementEmissionsCalculationDTO calculatedEmissions =
            measurementN2OEmissionsCalculationService.calculateEmissions(
            measurementN2OEmissionsCalculationParamsDTO);

        MeasurementEmissionsCalculationDTO measurementEmissionsCalculationDTO =
            EMISSION_CALCULATION_PARAMS_MAPPER.toMeasurementN2OEmissionsCalculationDTO(
                (MeasurementN2OEmissionPointEmission) measurementEmissionPointEmission);

        List<AerViolation> violations = new ArrayList<>();
        if (!calculatedEmissions.equals(measurementEmissionsCalculationDTO)) {
            violations.add(new AerViolation(MeasurementOfCO2Emissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.MEASUREMENT_INCORRECT_TOTAL_EMISSIONS));
        }
        return violations;
    }
}
