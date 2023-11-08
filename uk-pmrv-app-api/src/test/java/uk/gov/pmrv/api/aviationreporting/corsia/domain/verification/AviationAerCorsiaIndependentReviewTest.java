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
class AviationAerCorsiaIndependentReviewTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {
        final AviationAerCorsiaIndependentReview independentReview = AviationAerCorsiaIndependentReview.builder()
                .reviewResults("results")
                .name("name")
                .position("position")
                .email("dk@gmail.com")
                .line1("line1")
                .line2("line2")
                .city("city")
                .state("state")
                .postcode("postcode")
                .country("GR")
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaIndependentReview>> violations = validator.validate(independentReview);

        assertEquals(0, violations.size());
    }
}
