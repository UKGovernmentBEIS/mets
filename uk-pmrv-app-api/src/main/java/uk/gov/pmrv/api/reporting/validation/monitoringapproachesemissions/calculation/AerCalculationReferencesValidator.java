package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.validation.AerReferenceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AerCalculationReferencesValidator implements AerCalculationSourceStreamEmissionValidator {

    private final AerReferenceService aerReferenceService;

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {
        Aer aer = aerContainer.getAer();
        return Stream.of(
            // validate source stream
            aerReferenceService.validateExistenceInAer(
                aer.getSourceStreamsIds(),
                List.of(sourceStreamEmission.getSourceStream()),
                AerReferenceService.Rule.SOURCE_STREAM_EXISTS),

            // validate emission sources
            aerReferenceService.validateExistenceInAer(
                aer.getEmissionSourcesIds(),
                sourceStreamEmission.getEmissionSources(),
                AerReferenceService.Rule.EMISSION_SOURCES_EXIST)
        )
            .filter(Optional::isPresent)
            .flatMap(Optional::stream)
            .map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[]{};
                return new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                    p.getLeft(),
                    data);
            })
            .toList();
    }
}
