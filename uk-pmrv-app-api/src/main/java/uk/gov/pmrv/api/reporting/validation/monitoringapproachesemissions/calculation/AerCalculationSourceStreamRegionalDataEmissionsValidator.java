package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.RegionalInventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationInventoryEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationRegionalDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.service.RegionalInventoryDataService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AerCalculationSourceStreamRegionalDataEmissionsValidator extends
    SourceStreamParamCalcMethodEmissionsValidator {

    private final RegionalInventoryDataService regionalInventoryDataService;
    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    public AerCalculationSourceStreamRegionalDataEmissionsValidator(EmissionsCalculationService emissionsCalculationService,
                                                                    RegionalInventoryDataService regionalInventoryDataService) {
        super(emissionsCalculationService);
        this.regionalInventoryDataService = regionalInventoryDataService;
    }

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, SourceStreamType sourceStreamType,
                                       Year reportingYear) {
        CalculationRegionalDataCalculationMethod regionalDataCalculationMethod = 
            (CalculationRegionalDataCalculationMethod) sourceStreamEmission.getParameterCalculationMethod();

        List<AerViolation> violations = new ArrayList<>();

        RegionalInventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams =
            regionalInventoryDataService.getRegionalInventoryEmissionCalculationParams(reportingYear, regionalDataCalculationMethod.getLocalZoneCode())
                .orElse(null);
        CalculationInventoryEmissionCalculationParamValues calcMethodEmissionCalculationParams =
            regionalDataCalculationMethod.getEmissionCalculationParamValues();

        validateEmissionCalculationParams(calcMethodEmissionCalculationParams, inventoryEmissionCalculationParams)
                .ifPresent(violations::add);

        if(inventoryEmissionCalculationParams != null) {
            EmissionsCalculationParamsDTO emissionsCalculationParams = EMISSION_CALCULATION_PARAMS_MAPPER.toEmissionsCalculationParams(
                inventoryEmissionCalculationParams,
                regionalDataCalculationMethod.getCalculationActivityDataCalculationMethod(),
                sourceStreamEmission.getBiomassPercentages(),
                sourceStreamType,
                regionalDataCalculationMethod.getFuelMeteringConditionType()
            );

            validateEmissionsCalculation(emissionsCalculationParams, calcMethodEmissionCalculationParams).ifPresent(violations::add);
        }

        validateManuallyProvidedTotalSustainableBiomassEmissionsExist(calcMethodEmissionCalculationParams, sourceStreamEmission.getBiomassPercentages())
            .ifPresent(violations::add);

        return violations;
    }

    @Override
    public CalculationParameterCalculationMethodType getParameterCalculationMethodType() {
        return CalculationParameterCalculationMethodType.REGIONAL_DATA;
    }

    private Optional<AerViolation> validateEmissionCalculationParams(CalculationInventoryEmissionCalculationParamValues calcMethodEmissionCalculationParams,
                                                                     RegionalInventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams) {
        if (inventoryEmissionCalculationParams == null) {
            return Optional.of(
                    new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(), AerViolation.AerViolationMessage.CALCULATION_INVALID_CALCULATION_PARAMETERS));
        }

        RegionalInventoryEmissionCalculationParamsDTO calcMethodInventoryEmissionCalculationParams =
                EMISSION_CALCULATION_PARAMS_MAPPER.toRegionalInventoryEmissionCalculationParams(calcMethodEmissionCalculationParams);

        RegionalInventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParamsForComparison =  EMISSION_CALCULATION_PARAMS_MAPPER
                .toRegionalInventoryEmissionCalculationParamsDTO(inventoryEmissionCalculationParams);

        return !calcMethodInventoryEmissionCalculationParams.equals(inventoryEmissionCalculationParamsForComparison)
            ? Optional.of(
            new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(), AerViolation.AerViolationMessage.CALCULATION_INVALID_CALCULATION_PARAMETERS)
        )
            : Optional.empty();
    }
}
