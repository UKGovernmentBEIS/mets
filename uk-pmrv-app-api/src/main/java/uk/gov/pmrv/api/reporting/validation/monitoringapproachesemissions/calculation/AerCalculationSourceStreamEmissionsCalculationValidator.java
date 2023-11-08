package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AerCalculationSourceStreamEmissionsCalculationValidator implements AerCalculationSourceStreamEmissionValidator {

    private final List<AerCalculationSourceStreamParamCalcMethodEmissionsValidator> sourceStreamParamCalcMethodEmissionsValidators;
    
    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {

        List<AerViolation> violations = new ArrayList<>();

        CalculationParameterCalculationMethod parameterCalculationMethod = sourceStreamEmission.getParameterCalculationMethod();
        CalculationParameterCalculationMethodType paramCalculationMethodType = parameterCalculationMethod.getType();
        SourceStreamType sourceStreamType = getSourceStreamType(sourceStreamEmission, aerContainer);

        getParamCalcMethodEmissionsValidator(paramCalculationMethodType)
            .map(validator -> validator.validate(sourceStreamEmission, sourceStreamType, aerContainer.getReportingYear()))
            .ifPresent(violations::addAll);

        return violations;
    }

    private Optional<AerCalculationSourceStreamParamCalcMethodEmissionsValidator> getParamCalcMethodEmissionsValidator(
        CalculationParameterCalculationMethodType paramCalculationMethodType) {
        return sourceStreamParamCalcMethodEmissionsValidators.stream()
            .filter(validator -> validator.getParameterCalculationMethodType().equals(paramCalculationMethodType))
            .findAny();
    }

    private SourceStreamType getSourceStreamType(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {
        String sourceStreamId = sourceStreamEmission.getSourceStream();
        List<SourceStream> sourceStreams = aerContainer.getAer().getSourceStreams().getSourceStreams();
        return sourceStreams.stream()
            .filter(ss -> sourceStreamId.equals(ss.getId()))
            .findFirst()
            .map(SourceStream::getType)
            .orElse(null);
    }
}
