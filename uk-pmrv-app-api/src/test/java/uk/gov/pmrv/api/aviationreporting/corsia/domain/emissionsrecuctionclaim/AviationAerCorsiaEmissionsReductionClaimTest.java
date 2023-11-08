package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsrecuctionclaim;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaimDetails;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaEmissionsReductionClaimTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_no_emissions_reduction_claim_valid() {

        final AviationAerCorsiaEmissionsReductionClaim emissionsReductionClaim = AviationAerCorsiaEmissionsReductionClaim.builder()
                .exist(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_emissions_reduction_claim_exist_valid() {

        final AviationAerCorsiaEmissionsReductionClaim emissionsReductionClaim = AviationAerCorsiaEmissionsReductionClaim.builder()
                .exist(Boolean.TRUE)
                .emissionsReductionClaimDetails(AviationAerCorsiaEmissionsReductionClaimDetails.builder()
                        .cefFiles(Set.of(UUID.randomUUID()))
                        .totalEmissions(BigDecimal.valueOf(10.234))
                        .noDoubleCountingDeclarationFiles(Set.of(UUID.randomUUID()))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_emissions_reduction_claim_exist_invalid() {

        final AviationAerCorsiaEmissionsReductionClaim emissionsReductionClaim = AviationAerCorsiaEmissionsReductionClaim.builder()
                .exist(Boolean.TRUE)
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.emissionsReductionClaim.exist}");
    }
}
