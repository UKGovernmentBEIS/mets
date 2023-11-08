package uk.gov.pmrv.api.aviationreporting.corsia.domain.confidentiality;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaConfidentialityTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_total_emissions_aggregated_state_false_valid() {

        final AviationAerCorsiaConfidentiality confidentiality = AviationAerCorsiaConfidentiality.builder()
                .totalEmissionsPublished(Boolean.FALSE)
                .aggregatedStatePairDataPublished(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaConfidentiality>> violations = validator.validate(confidentiality);

        assertEquals(0, violations.size());
    }

    @Test
    void when_total_emissions_true_aggregated_state_false_valid() {

        final AviationAerCorsiaConfidentiality confidentiality = AviationAerCorsiaConfidentiality.builder()
                .totalEmissionsPublished(Boolean.TRUE)
                .totalEmissionsExplanation("total emissions explanation")
                .aggregatedStatePairDataPublished(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaConfidentiality>> violations = validator.validate(confidentiality);

        assertEquals(0, violations.size());
    }

    @Test
    void when_total_emissions_false_aggregated_state_true_valid() {

        final AviationAerCorsiaConfidentiality confidentiality = AviationAerCorsiaConfidentiality.builder()
                .totalEmissionsPublished(Boolean.FALSE)
                .aggregatedStatePairDataPublished(Boolean.TRUE)
                .aggregatedStatePairDataExplanation("aggregated state pairs explanation")
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaConfidentiality>> violations = validator.validate(confidentiality);

        assertEquals(0, violations.size());
    }

    @Test
    void when_total_emissions_true_aggregated_state_true_valid() {

        final AviationAerCorsiaConfidentiality confidentiality = AviationAerCorsiaConfidentiality.builder()
                .totalEmissionsPublished(Boolean.TRUE)
                .totalEmissionsExplanation("total emissions explanation")
                .totalEmissionsDocuments(Set.of(UUID.randomUUID()))
                .aggregatedStatePairDataPublished(Boolean.TRUE)
                .aggregatedStatePairDataExplanation("aggregated state pairs explanation")
                .aggregatedStatePairDataDocuments(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaConfidentiality>> violations = validator.validate(confidentiality);

        assertEquals(0, violations.size());
    }

    @Test
    void when_total_emissions_aggregated_state_false_invalid() {

        final AviationAerCorsiaConfidentiality confidentiality = AviationAerCorsiaConfidentiality.builder()
                .totalEmissionsPublished(Boolean.FALSE)
                .totalEmissionsExplanation("total emissions explanation")
                .aggregatedStatePairDataPublished(Boolean.FALSE)
                .aggregatedStatePairDataExplanation("aggregated state pairs explanation")
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaConfidentiality>> violations = validator.validate(confidentiality);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{aviationAer.corsia.confidentiality.totalEmissionsExplanation}", "{aviationAer.corsia.confidentiality.aggregatedStatePairDataExplanation}");
    }

    @Test
    void when_total_emissions_true_aggregated_state_true_invalid() {

        final AviationAerCorsiaConfidentiality confidentiality = AviationAerCorsiaConfidentiality.builder()
                .totalEmissionsPublished(Boolean.TRUE)
                .totalEmissionsDocuments(Set.of(UUID.randomUUID()))
                .aggregatedStatePairDataPublished(Boolean.TRUE)
                .aggregatedStatePairDataDocuments(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaConfidentiality>> violations = validator.validate(confidentiality);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{aviationAer.corsia.confidentiality.totalEmissionsExplanation}", "{aviationAer.corsia.confidentiality.aggregatedStatePairDataExplanation}");
    }
}
