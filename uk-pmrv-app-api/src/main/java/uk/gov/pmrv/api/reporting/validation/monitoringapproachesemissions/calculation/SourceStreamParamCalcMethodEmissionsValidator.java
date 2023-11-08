package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public abstract class SourceStreamParamCalcMethodEmissionsValidator implements AerCalculationSourceStreamParamCalcMethodEmissionsValidator {

    private final EmissionsCalculationService emissionsCalculationService;
    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    public Optional<AerViolation> validateEmissionsCalculation(EmissionsCalculationParamsDTO emissionsCalculationParams,
                                                               CalculationEmissionCalculationParamValues emissionCalculationParamValues) {
        EmissionsCalculationDTO emissions = emissionsCalculationService.calculateEmissions(emissionsCalculationParams);
        EmissionsCalculationDTO calcMethodProvidedEmissions = EMISSION_CALCULATION_PARAMS_MAPPER
            .toEmissionsCalculationDTO(emissionCalculationParamValues);

        return !emissions.equals(calcMethodProvidedEmissions)
            ? Optional.of(new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(), AerViolation.AerViolationMessage.CALCULATION_INCORRECT_TOTAL_EMISSIONS))
            : Optional.empty();
    }

    public Optional<AerViolation> validateManuallyProvidedTotalSustainableBiomassEmissionsExist(CalculationEmissionCalculationParamValues emissionCalculationParamValues,
                                                                                                BiomassPercentages sourceStreamBiomassPercentages) {
        if(Boolean.FALSE.equals(emissionCalculationParamValues.getCalculationCorrect())) {
            ManuallyProvidedEmissions manuallyProvidedEmissions = emissionCalculationParamValues.getProvidedEmissions();
            if(Boolean.TRUE.equals(sourceStreamBiomassPercentages.getContains()) && manuallyProvidedEmissions.getTotalProvidedSustainableBiomassEmissions() == null) {
                return Optional.of(new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(), AerViolation.AerViolationMessage.TOTAL_SUSTAINABLE_BIOMASS_EMISSION_ARE_MISSING));
            }
        }
        return Optional.empty();
    }

}
