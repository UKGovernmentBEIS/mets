package uk.gov.pmrv.api.aviationreporting.ukets.domain.saf;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerSafTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_saf_exist_then_valid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(0, violations.size());
    }

    @Test
    void when_saf_not_exist_then_valid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(0, violations.size());
    }

    @Test
    void when_saf_not_exist_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.FALSE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.saf.exist}");
    }

    @Test
    void when_saf_exist_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.saf.exist}");
    }

    @Test
    void when_saf_purchases_empty_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of())
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_total_saf_mass_not_exist_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_saf_emissions_factor_not_exist_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName1", "batch number1", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID())),
                                createSafPurchase("fuelName2", "batch number2", BigDecimal.valueOf(3.14), SustainabilityCriteriaEvidenceType.FUEL_SUPPLIER_REPORT, Set.of())))
                        .totalSafMass(BigDecimal.TEN)
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.saf.emissionsFactor}", "must not be null");
    }

    @Test
    void when_saf_total_emissions_reduction_claim_not_exist_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_saf_declaration_file_not_exist_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_total_saf_mass_zero_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.ZERO)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_total_emissions_reduction_claim_zero_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.ZERO)
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_total_emissions_reduction_claim_more_decimal_digits_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.12345))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissions_factor_wrong_value_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(2.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_saf_purchase_fuel_name_batch_number_blank_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("", "", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(2, violations.size());
    }

    @Test
    void when_saf_purchase_mass_zero_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ZERO, SustainabilityCriteriaEvidenceType.VOLUNTARY_CERTIFICATION_SCHEME_MARKING, Set.of())))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_saf_purchase_mass_more_decimal_digits_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.valueOf(2.1234), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of())))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    @Test
    void when_saf_purchase_sustainability_criteria_evidence_other_then_valid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchaseWithDescription("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.OTHER, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(0, violations.size());
    }

    @Test
    void when_saf_purchase_sustainability_criteria_evidence_other_no_description_then_invalid() {
        final AviationAerSaf saf = AviationAerSaf.builder()
                .exist(Boolean.TRUE)
                .safDetails(AviationAerSafDetails.builder()
                        .purchases(List.of(createSafPurchase("fuelName", "batch number", BigDecimal.ONE, SustainabilityCriteriaEvidenceType.OTHER, Set.of(UUID.randomUUID()))))
                        .totalSafMass(BigDecimal.TEN)
                        .emissionsFactor(BigDecimal.valueOf(3.15))
                        .totalEmissionsReductionClaim(BigDecimal.valueOf(31.5))
                        .noDoubleCountingDeclarationFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerSaf>> violations = validator.validate(saf);

        assertEquals(1, violations.size());
    }

    private AviationAerSafPurchase createSafPurchase(String fuelName, String batchNumber, BigDecimal safMass,
                                                     SustainabilityCriteriaEvidenceType type, Set<UUID> evidenceFiles) {
        return AviationAerSafPurchase.builder()
                .fuelName(fuelName)
                .batchNumber(batchNumber)
                .safMass(safMass)
                .sustainabilityCriteriaEvidenceType(type)
                .evidenceFiles(evidenceFiles)
                .build();
    }

    private AviationAerSafPurchase createSafPurchaseWithDescription(String fuelName, String batchNumber, BigDecimal safMass,
                                                     SustainabilityCriteriaEvidenceType type, Set<UUID> evidenceFiles) {
        return AviationAerSafPurchase.builder()
                .fuelName(fuelName)
                .batchNumber(batchNumber)
                .safMass(safMass)
                .sustainabilityCriteriaEvidenceType(type)
                .otherSustainabilityCriteriaEvidenceDescription("description")
                .evidenceFiles(evidenceFiles)
                .build();
    }
}
