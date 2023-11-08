package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationInventoryEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNationalInventoryDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.service.NationalInventoryDataService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AerCalculationSourceStreamNationalInventoryDataEmissionsValidator extends
    SourceStreamParamCalcMethodEmissionsValidator {

    private final NationalInventoryDataService nationalInventoryDataService;
    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    public AerCalculationSourceStreamNationalInventoryDataEmissionsValidator(EmissionsCalculationService emissionsCalculationService,
                                                                             NationalInventoryDataService nationalInventoryDataService) {
        super(emissionsCalculationService);
        this.nationalInventoryDataService = nationalInventoryDataService;
    }

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, SourceStreamType sourceStreamType,
                                       Year reportingYear) {
        CalculationNationalInventoryDataCalculationMethod paramCalculationMethod =
            (CalculationNationalInventoryDataCalculationMethod) sourceStreamEmission.getParameterCalculationMethod();

        List<AerViolation> violations = new ArrayList<>();

        InventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams = nationalInventoryDataService
            .getEmissionCalculationParams(reportingYear, paramCalculationMethod.getMainActivitySector(), paramCalculationMethod.getFuel());
        CalculationInventoryEmissionCalculationParamValues calcMethodEmissionCalculationParams =
            paramCalculationMethod.getEmissionCalculationParamValues();

        validateEmissionCalculationParams(calcMethodEmissionCalculationParams, inventoryEmissionCalculationParams).ifPresent(violations::add);

        if(inventoryEmissionCalculationParams != null) {
            EmissionsCalculationParamsDTO emissionsCalculationParams = EMISSION_CALCULATION_PARAMS_MAPPER.toEmissionsCalculationParams(
                inventoryEmissionCalculationParams,
                paramCalculationMethod.getCalculationActivityDataCalculationMethod(),
                sourceStreamEmission.getBiomassPercentages(),
                sourceStreamType
            );

            validateEmissionsCalculation(emissionsCalculationParams, calcMethodEmissionCalculationParams)
                .ifPresent(violations::add);
        }

        validateManuallyProvidedTotalSustainableBiomassEmissionsExist(calcMethodEmissionCalculationParams, sourceStreamEmission.getBiomassPercentages())
            .ifPresent(violations::add);

        return violations;
    }

    @Override
    public CalculationParameterCalculationMethodType getParameterCalculationMethodType() {
        return CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA;
    }

    private Optional<AerViolation> validateEmissionCalculationParams(CalculationInventoryEmissionCalculationParamValues calcMethodEmissionCalculationParams,
                                                                     InventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams) {
        InventoryEmissionCalculationParamsDTO calcMethodInventoryEmissionCalculationParams =
            EMISSION_CALCULATION_PARAMS_MAPPER.toInventoryEmissionCalculationParams(calcMethodEmissionCalculationParams);

        return !calcMethodInventoryEmissionCalculationParams.equals(inventoryEmissionCalculationParams)
            ? Optional.of(
                new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(), AerViolation.AerViolationMessage.CALCULATION_INVALID_CALCULATION_PARAMETERS)
            )
            : Optional.empty();
    }
}
