package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsSubmittedEmissionsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_confidential_is_true_then_valid() {

        AviationAerTotalEmissionsConfidentiality aviationAerTotalEmissionsConfidentiality =
            AviationAerTotalEmissionsConfidentiality.builder()
                .confidential(true)
                .build();

        Set<ConstraintViolation<AviationAerTotalEmissionsConfidentiality>> violations = validator
            .validate(aviationAerTotalEmissionsConfidentiality);

        assertEquals(0, violations.size());
    }

    @Test
    void when_confidential_is_false_then_valid() {

        AviationAerTotalEmissionsConfidentiality aviationAerTotalEmissionsConfidentiality =
            AviationAerTotalEmissionsConfidentiality.builder()
                .confidential(false)
                .build();

        Set<ConstraintViolation<AviationAerTotalEmissionsConfidentiality>> violations = validator
            .validate(aviationAerTotalEmissionsConfidentiality);

        assertEquals(0, violations.size());
    }

    @Test
    void when_confidential_is_null_then_invalid() {
        AviationAerTotalEmissionsConfidentiality aviationAerTotalEmissionsConfidentiality =
            AviationAerTotalEmissionsConfidentiality.builder()
                .confidential(null)
                .build();

        Set<ConstraintViolation<AviationAerTotalEmissionsConfidentiality>> violations = validator
            .validate(aviationAerTotalEmissionsConfidentiality);

        assertEquals(1, violations.size());
    }

    @Test
    void when_scheme_year_fields_are_invalid_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(-1)
            .standardFuelEmissions(BigDecimal.valueOf(-1))
            .totalEmissions(null)
            .reductionClaimEmissions(BigDecimal.valueOf(-2))
            .build();

        AviationAerUkEtsSubmittedEmissions totalEmissions = new AviationAerUkEtsSubmittedEmissions();
        totalEmissions.setAviationAerTotalEmissions(schemeYear);

        Set<ConstraintViolation<AviationAerUkEtsSubmittedEmissions>> violations = validator.validate(totalEmissions);

        assertEquals(4, violations.size());
    }

    @Test
    void when_scheme_year_fields_are_valid_then_valid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(BigDecimal.valueOf(200.123))
            .totalEmissions(BigDecimal.valueOf(100))
            .build();

        AviationAerUkEtsSubmittedEmissions totalEmissions = new AviationAerUkEtsSubmittedEmissions();
        totalEmissions.setAviationAerTotalEmissions(schemeYear);

        Set<ConstraintViolation<AviationAerUkEtsSubmittedEmissions>> violations = validator.validate(totalEmissions);

        assertEquals(0, violations.size());
    }

    @Test
    void when_numFlightsCoveredByUkEts_is_positive_then_valid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(0, violations.size());
    }

    @Test
    void when_numFlightsCoveredByUkEts_is_negative_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(-1)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_numFlightsCoveredByUkEts_is_null_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(null)
            .standardFuelEmissions(BigDecimal.valueOf(1))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissionsFromStandardFuels_is_positive_and_has_max_3_decimal_places_then_valid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(new BigDecimal("100.123"))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(0, violations.size());
    }

    @Test
    void when_emissionsFromStandardFuels_is_positive_and_has_more_than_3_decimal_places_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(new BigDecimal("100.1234"))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissionsFromStandardFuels_is_negative_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(-1))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissionsFromStandardFuels_is_null_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(null)
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissionsReductionClaimForTheSchemeYear_is_positive_and_has_max_3_decimal_places_then_valid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(BigDecimal.valueOf(100.123))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(0, violations.size());
    }

    @Test
    void when_emissionsReductionClaimForTheSchemeYear_is_positive_and_has_more_than_3_decimal_places_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(BigDecimal.valueOf(100.1234))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissionsReductionClaimForTheSchemeYear_is_negative_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(BigDecimal.valueOf(-1))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissionsReductionClaimForTheSchemeYear_is_null_then_valid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(null)
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(0, violations.size());
    }

    @Test
    void when_totalEmissionsForTheSchemeYear_is_positive_then_valid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(BigDecimal.valueOf(100.123))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(0, violations.size());
    }

    @Test
    void when_totalEmissionsForTheSchemeYear_is_negative_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(BigDecimal.valueOf(100.123))
            .totalEmissions(BigDecimal.valueOf(-200))
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_totalEmissionsForTheSchemeYear_is_null_then_invalid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .totalEmissions(null)
            .build();

        Set<ConstraintViolation<AviationAerTotalEmissions>> violations = validator.validate(schemeYear);

        assertEquals(1, violations.size());
    }

    @Test
    void when_all_fields_are_valid_then_valid() {
        AviationAerTotalEmissions schemeYear = AviationAerTotalEmissions.builder()
            .numFlightsCoveredByUkEts(10)
            .standardFuelEmissions(BigDecimal.valueOf(100.123))
            .reductionClaimEmissions(BigDecimal.valueOf(200.123))
            .totalEmissions(BigDecimal.valueOf(200))
            .build();

        AviationAerUkEtsSubmittedEmissions totalEmissions = new AviationAerUkEtsSubmittedEmissions();
        totalEmissions.setAviationAerTotalEmissions(schemeYear);

        Set<ConstraintViolation<AviationAerUkEtsSubmittedEmissions>> violations = validator.validate(totalEmissions);

        assertEquals(0, violations.size());
    }
}