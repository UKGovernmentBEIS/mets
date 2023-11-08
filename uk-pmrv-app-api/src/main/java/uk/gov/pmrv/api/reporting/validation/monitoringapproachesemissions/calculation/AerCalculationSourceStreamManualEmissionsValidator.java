package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class AerCalculationSourceStreamManualEmissionsValidator extends SourceStreamParamCalcMethodEmissionsValidator {

    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    public AerCalculationSourceStreamManualEmissionsValidator(EmissionsCalculationService emissionsCalculationService) {
        super(emissionsCalculationService);
    }

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, SourceStreamType sourceStreamType, Year reportingYear) {
        CalculationManualCalculationMethod manualCalculationMethod =
            (CalculationManualCalculationMethod) sourceStreamEmission.getParameterCalculationMethod();

        List<AerViolation> violations = new ArrayList<>();

        CalculationManualEmissionCalculationParamValues emissionCalculationParamValues = manualCalculationMethod.getEmissionCalculationParamValues();

        EmissionsCalculationParamsDTO emissionsCalculationParams = EMISSION_CALCULATION_PARAMS_MAPPER.toEmissionsCalculationParams(
            emissionCalculationParamValues,
            manualCalculationMethod.getCalculationActivityDataCalculationMethod(),
            sourceStreamEmission.getBiomassPercentages(),
            sourceStreamType
        );

        validateEmissionsCalculation(emissionsCalculationParams, emissionCalculationParamValues).ifPresent(violations::add);
        validateManuallyProvidedTotalSustainableBiomassEmissionsExist(emissionCalculationParamValues, sourceStreamEmission.getBiomassPercentages()).ifPresent(violations::add);

        return violations;
    }

    @Override
    public CalculationParameterCalculationMethodType getParameterCalculationMethodType() {
        return CalculationParameterCalculationMethodType.MANUAL;
    }
}
