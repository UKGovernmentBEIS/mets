package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;
import uk.gov.pmrv.api.reporting.validation.AerReferenceService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AerCalculationPfcReferencesValidator implements AerCalculationOfPfcSourceStreamEmissionValidator {

    private final AerReferenceService aerReferenceService;

    @Override
    public List<AerViolation> validate(PfcSourceStreamEmission pfcSourceStreamEmission, AerContainer aerContainer) {
        Aer aer = aerContainer.getAer();
        return Stream.of(
                // validate source stream
                aerReferenceService.validateExistenceInAer(
                    aer.getSourceStreamsIds(),
                    Set.of(pfcSourceStreamEmission.getSourceStream()),
                    AerReferenceService.Rule.SOURCE_STREAM_EXISTS),

                // validate emission sources
                aerReferenceService.validateExistenceInAer(
                    aer.getEmissionSourcesIds(),
                    pfcSourceStreamEmission.getEmissionSources(),
                    AerReferenceService.Rule.EMISSION_SOURCES_EXIST)
            )
            .filter(Optional::isPresent)
            .flatMap(Optional::stream)
            .map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[]{};
                return new AerViolation(CalculationOfPfcEmissions.class.getSimpleName(),
                    p.getLeft(),
                    data);
            })
            .collect(Collectors.toList());
    }
}
