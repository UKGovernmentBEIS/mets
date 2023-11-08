package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.fallback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.validation.AerReferenceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AerFallbackReferencesValidator implements AerFallbackEmissionValidator {

    private final AerReferenceService aerReferenceService;

    @Override
    public List<AerViolation> validate(FallbackEmissions fallbackEmission, AerContainer aerContainer) {
        Aer aer = aerContainer.getAer();
        return Stream.of(
                // validate source streams
                aerReferenceService.validateExistenceInAer(
                    aer.getSourceStreamsIds(),
                    fallbackEmission.getSourceStreams(),
                    AerReferenceService.Rule.SOURCE_STREAM_EXISTS)
            )
            .filter(Optional::isPresent)
            .flatMap(Optional::stream)
            .map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[]{};
                return new AerViolation(FallbackEmissions.class.getSimpleName(),
                    p.getLeft(),
                    data);
            })
            .toList();
    }
}
