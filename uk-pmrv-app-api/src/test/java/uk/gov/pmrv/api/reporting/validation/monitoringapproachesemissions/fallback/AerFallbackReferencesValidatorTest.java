package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.fallback;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.validation.AerReferenceService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerFallbackReferencesValidatorTest {

    @Mock
    private AerReferenceService aerReferenceService;

    @InjectMocks
    private AerFallbackReferencesValidator aerFallbackReferencesValidator;

    @Test
    void validate() {
        Aer aer = Aer.builder()
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(
                    SourceStream.builder()
                        .id("1")
                        .build(),
                    SourceStream.builder()
                        .id("2")
                        .build()
                ))
                .build())
            .build();

        AerContainer aerContainer = AerContainer.builder()
            .aer(aer)
            .build();

        FallbackEmissions fallbackEmission = FallbackEmissions.builder()
            .sourceStreams(Set.of("1", "2"))
            .build();

        when(aerReferenceService.validateExistenceInAer(aer.getSourceStreamsIds(),
            fallbackEmission.getSourceStreams(), AerReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.empty());

        List<AerViolation> violations = aerFallbackReferencesValidator.validate(fallbackEmission, aerContainer);

        assertThat(violations).isEmpty();

    }

    @Test
    void validateWithViolation() {
        Aer aer = Aer.builder()
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(
                    SourceStream.builder()
                        .id("1")
                        .build(),
                    SourceStream.builder()
                        .id("2")
                        .build()
                ))
                .build())
            .build();

        AerContainer aerContainer = AerContainer.builder()
            .aer(aer)
            .build();

        FallbackEmissions fallbackEmission = FallbackEmissions.builder()
            .sourceStreams(Set.of("1", "2"))
            .build();

        when(aerReferenceService.validateExistenceInAer(aer.getSourceStreamsIds(),
            fallbackEmission.getSourceStreams(), AerReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.of(Pair.of(AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM,
                List.of("1", "2"))));

        List<AerViolation> violations = aerFallbackReferencesValidator.validate(fallbackEmission, aerContainer);

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getMessage())
            .isEqualTo(AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM.getMessage());
        assertThat(violations).extracting(AerViolation::getSectionName).containsOnly(FallbackEmissions.class.getSimpleName());
    }

    @Test
    void validateWithEmptySourceStreams() {
        Aer aer = Aer.builder()
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(
                    SourceStream.builder()
                        .id("1")
                        .build(),
                    SourceStream.builder()
                        .id("2")
                        .build()
                ))
                .build())
            .build();

        AerContainer aerContainer = AerContainer.builder()
            .aer(aer)
            .build();

        FallbackEmissions fallbackEmission = FallbackEmissions.builder()
            .sourceStreams(Set.of())
            .build();

        when(aerReferenceService.validateExistenceInAer(aer.getSourceStreamsIds(),
            fallbackEmission.getSourceStreams(), AerReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.empty());

        List<AerViolation> violations = aerFallbackReferencesValidator.validate(fallbackEmission, aerContainer);

        assertThat(violations).isEmpty();
    }
}