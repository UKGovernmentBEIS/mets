package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.validation.AerReferenceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AerMeasurementReferencesValidator implements AerMeasurementEmissionPointEmissionValidator {

    private final AerReferenceService aerReferenceService;

    @Override
    public List<AerViolation> validate(MeasurementEmissionPointEmission emissionPointEmission,
                                       AerContainer aerContainer) {
        Aer aer = aerContainer.getAer();
        return Stream.of(
                // validate source stream
                aerReferenceService.validateExistenceInAer(
                    aer.getSourceStreamsIds(),
                    emissionPointEmission.getSourceStreams(),
                    AerReferenceService.Rule.SOURCE_STREAM_EXISTS),

                // validate emission sources
                aerReferenceService.validateExistenceInAer(
                    aer.getEmissionSourcesIds(),
                    emissionPointEmission.getEmissionSources(),
                    AerReferenceService.Rule.EMISSION_SOURCES_EXIST),

                // validate emission points
                aerReferenceService.validateExistenceInAer(
                    aer.getEmissionPointsIds(),
                    List.of(emissionPointEmission.getEmissionPoint()),
                    AerReferenceService.Rule.EMISSION_POINTS_EXIST)
            )
            .filter(Optional::isPresent)
            .flatMap(Optional::stream)
            .map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[]{};
                return new AerViolation(MeasurementEmissionPointEmission.class.getSimpleName(),
                    p.getLeft(),
                    data);
            })
            .collect(Collectors.toList());
    }
}
