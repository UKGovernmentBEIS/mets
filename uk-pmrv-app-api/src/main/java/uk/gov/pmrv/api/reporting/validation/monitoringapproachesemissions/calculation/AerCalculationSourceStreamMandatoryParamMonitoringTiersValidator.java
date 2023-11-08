package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.AerViolation.AerViolationMessage;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerCalculationSourceStreamMandatoryParamMonitoringTiersValidator implements AerCalculationSourceStreamEmissionValidator {

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        String sourceStreamId = sourceStreamEmission.getSourceStream();
        List<SourceStream> sourceStreams = aerContainer.getAer().getSourceStreams().getSourceStreams();
        Optional<SourceStream> sourceStream = sourceStreams.stream()
            .filter(ss -> sourceStreamId.equals(ss.getId()))
            .findFirst();
        SourceStreamCategory sourceStreamCategory = sourceStream
            .map(SourceStream::getType)
            .map(SourceStreamType::getCategory)
            .orElse(null);

        if (sourceStreamCategory == null) {
            return List.of(
                new AerViolation(
                    CalculationOfCO2Emissions.class.getSimpleName(),
                    AerViolationMessage.CALCULATION_INVALID_PARAMETER_MONITORING_TIER,
                    String.format("Cannot map source stream id %s", sourceStreamId)
                )
            );
        }

        /*
          collect all parameter types for which a monitoring tier exists in the calculation source stream emission object,
          except BIOMASS_FRACTION type for which a separate validation exists
         */
        Set<CalculationParameterType> monitoringTierParameterTypes = sourceStreamEmission.getParameterMonitoringTiers().stream()
            .map(CalculationParameterMonitoringTier::getType)
            .filter(calculationParameterType -> calculationParameterType != CalculationParameterType.BIOMASS_FRACTION)
            .collect(Collectors.toSet());

        Set<CalculationParameterType> ssCategApplicableCalcParamTypes = sourceStreamCategory.getApplicableCalculationParameterTypes();
        if (!monitoringTierParameterTypes.equals(ssCategApplicableCalcParamTypes)) {
            violations.add(
                new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                    AerViolation.AerViolationMessage.CALCULATION_INVALID_PARAMETER_MONITORING_TIER,
                    String.format("%s - %s", sourceStreamId, ssCategApplicableCalcParamTypes.stream().map(Enum::name).collect(Collectors.joining(",")))));
        }
        return violations;
    }
}
