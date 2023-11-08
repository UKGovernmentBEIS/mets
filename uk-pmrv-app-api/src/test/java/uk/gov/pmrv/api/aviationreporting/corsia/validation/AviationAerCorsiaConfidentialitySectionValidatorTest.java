package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.confidentiality.AviationAerCorsiaConfidentiality;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaConfidentialitySectionValidatorTest {

    @InjectMocks
    private AviationAerCorsiaConfidentialitySectionValidator validator;

    @Test
    void when_documents_exist_valid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .confidentiality(AviationAerCorsiaConfidentiality.builder()
                                .totalEmissionsPublished(Boolean.TRUE)
                                .totalEmissionsExplanation("total emissions explanation")
                                .totalEmissionsDocuments(Set.of(UUID.randomUUID()))
                                .aggregatedStatePairDataPublished(Boolean.TRUE)
                                .aggregatedStatePairDataExplanation("aggregated state pairs explanation")
                                .aggregatedStatePairDataDocuments(Set.of(UUID.randomUUID()))
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void when_documents_not_exist_valid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .confidentiality(AviationAerCorsiaConfidentiality.builder()
                                .totalEmissionsPublished(Boolean.TRUE)
                                .totalEmissionsExplanation("total emissions explanation")
                                .aggregatedStatePairDataPublished(Boolean.TRUE)
                                .aggregatedStatePairDataExplanation("aggregated state pairs explanation")
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void when_documents_not_exist_explanations_not_exist_valid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .confidentiality(AviationAerCorsiaConfidentiality.builder()
                                .totalEmissionsPublished(Boolean.FALSE)
                                .aggregatedStatePairDataPublished(Boolean.FALSE)
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void when_documents_exist_invalid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .confidentiality(AviationAerCorsiaConfidentiality.builder()
                                .totalEmissionsPublished(Boolean.FALSE)
                                .totalEmissionsDocuments(Set.of(UUID.randomUUID()))
                                .aggregatedStatePairDataPublished(Boolean.FALSE)
                                .aggregatedStatePairDataDocuments(Set.of(UUID.randomUUID()))
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(2);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .contains(AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_EMISSIONS_DOCUMENTS.getMessage(),
                        AviationAerViolation.AviationAerViolationMessage.INVALID_AGGREGATED_STATE_PAIR_DOCUMENTS.getMessage());
    }
}
