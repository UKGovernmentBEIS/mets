package uk.gov.pmrv.api.verificationbody;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEmissionSchemeDTO;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationBodyDetailsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValid_whenOnlyNewFieldProvided() {
        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .accreditationReferenceNumber("ref")
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .build();

        VerificationBodyDetails obj = VerificationBodyDetails.builder()
                .verificationBodyEmissionSchemeDTOS(Set.of(verificationBodyEmissionSchemeDTO))
                .build();

        Set<ConstraintViolation<VerificationBodyDetails>> violations = validator.validate(obj);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldBeValid_whenOnlyOldFieldsProvided() {
        VerificationBodyDetails obj = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("ref")
                .emissionTradingSchemes(Set.of(EmissionTradingScheme.EU_ETS_INSTALLATIONS))
                .build();

        Set<ConstraintViolation<VerificationBodyDetails>> violations = validator.validate(obj);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldBeInvalid_whenBothOldAndNewProvided() {
        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .accreditationReferenceNumber("ref")
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .build();

        VerificationBodyDetails obj = VerificationBodyDetails.builder()
                .verificationBodyEmissionSchemeDTOS(Set.of(verificationBodyEmissionSchemeDTO))
                .accreditationReferenceNumber("ref")
                .emissionTradingSchemes(Set.of(EmissionTradingScheme.EU_ETS_INSTALLATIONS))
                .build();

        Set<ConstraintViolation<VerificationBodyDetails>> violations = validator.validate(obj);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldBeInvalid_whenOnlyOneOldFieldProvided() {
        VerificationBodyDetails obj = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("ref") // missing schemes
                .build();

        Set<ConstraintViolation<VerificationBodyDetails>> violations = validator.validate(obj);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldBeInvalid_whenNothingProvided() {
        VerificationBodyDetails obj = new VerificationBodyDetails();

        Set<ConstraintViolation<VerificationBodyDetails>> violations = validator.validate(obj);

        assertThat(violations).isNotEmpty();
    }
}
