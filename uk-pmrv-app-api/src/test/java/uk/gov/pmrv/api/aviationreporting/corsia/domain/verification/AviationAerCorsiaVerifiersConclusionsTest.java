package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaVerifiersConclusionsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {
        final AviationAerCorsiaVerifiersConclusions verifiersConclusions = AviationAerCorsiaVerifiersConclusions.builder()
                .dataQualityMateriality("data quality")
                .materialityThresholdType(MaterialityThresholdType.THRESHOLD_2_PER_CENT)
                .materialityThresholdMet(Boolean.FALSE)
                .emissionsReportConclusion("conclusion")
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifiersConclusions>> violations = validator.validate(verifiersConclusions);

        assertEquals(0, violations.size());
    }

    @Test
    void when_missing_fields_invalid() {
        final AviationAerCorsiaVerifiersConclusions verifiersConclusions = AviationAerCorsiaVerifiersConclusions.builder()
                .dataQualityMateriality("data quality")
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifiersConclusions>> violations = validator.validate(verifiersConclusions);

        assertEquals(3, violations.size());
    }
}
