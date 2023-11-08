package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafPurchase;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.SustainabilityCriteriaEvidenceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsSafSectionValidatorTest {

    @InjectMocks
    private AviationAerUkEtsSafSectionValidator validator;

    @Test
    void validate_saf_not_exist_valid() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .saf(AviationAerSaf.builder()
                                .exist(Boolean.FALSE)
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void validate_saf_exist_valid() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .saf(AviationAerSaf.builder()
                                .exist(Boolean.TRUE)
                                .safDetails(AviationAerSafDetails.builder()
                                        .purchases(List.of(createSafPurchase("fuel name 1", "batch number 1", BigDecimal.valueOf(12.345), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 2", "batch number 2", BigDecimal.valueOf(38.987), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 3", "batch number 3", BigDecimal.valueOf(57.683), SustainabilityCriteriaEvidenceType.FUEL_SUPPLIER_REPORT, Set.of())))
                                        .totalSafMass(BigDecimal.valueOf(109.015))
                                        .emissionsFactor(BigDecimal.valueOf(3.15))
                                        .totalEmissionsReductionClaim(BigDecimal.valueOf(343.397))
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void validate_saf_exist_invalid_total_mass() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .saf(AviationAerSaf.builder()
                                .exist(Boolean.TRUE)
                                .safDetails(AviationAerSafDetails.builder()
                                        .purchases(List.of(createSafPurchase("fuel name 1", "batch number 1", BigDecimal.valueOf(12.345), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 2", "batch number 2", BigDecimal.valueOf(38.987), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 3", "batch number 3", BigDecimal.valueOf(57.683), SustainabilityCriteriaEvidenceType.FUEL_SUPPLIER_REPORT, Set.of())))
                                        .totalSafMass(BigDecimal.valueOf(109.115))
                                        .emissionsFactor(BigDecimal.valueOf(3.15))
                                        .totalEmissionsReductionClaim(BigDecimal.valueOf(343.397))
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(1);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_SAF_MASS.getMessage());
    }

    @Test
    void validate_saf_exist_invalid_total_emissions_reduction_claim() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .saf(AviationAerSaf.builder()
                                .exist(Boolean.TRUE)
                                .safDetails(AviationAerSafDetails.builder()
                                        .purchases(List.of(createSafPurchase("fuel name 1", "batch number 1", BigDecimal.valueOf(12.345), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 2", "batch number 2", BigDecimal.valueOf(38.987), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 3", "batch number 3", BigDecimal.valueOf(57.683), SustainabilityCriteriaEvidenceType.FUEL_SUPPLIER_REPORT, Set.of())))
                                        .totalSafMass(BigDecimal.valueOf(109.015))
                                        .emissionsFactor(BigDecimal.valueOf(3.15))
                                        .totalEmissionsReductionClaim(BigDecimal.valueOf(343.399))
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(1);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_EMISSIONS_REDUCTION_CLAIM.getMessage());
    }

    @Test
    void validate_saf_exist_invalid_total_mass_and_emissions_reduction_claim() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .saf(AviationAerSaf.builder()
                                .exist(Boolean.TRUE)
                                .safDetails(AviationAerSafDetails.builder()
                                        .purchases(List.of(createSafPurchase("fuel name 1", "batch number 1", BigDecimal.valueOf(12.345), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 2", "batch number 2", BigDecimal.valueOf(38.987), SustainabilityCriteriaEvidenceType.SCREENSHOT_FROM_RTFO_REGISTRY, Set.of()),
                                                createSafPurchase("fuel name 3", "batch number 3", BigDecimal.valueOf(57.683), SustainabilityCriteriaEvidenceType.FUEL_SUPPLIER_REPORT, Set.of())))
                                        .totalSafMass(BigDecimal.valueOf(109.215))
                                        .emissionsFactor(BigDecimal.valueOf(3.15))
                                        .totalEmissionsReductionClaim(BigDecimal.valueOf(343.398))
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(2);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_SAF_MASS.getMessage(),
                        AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_EMISSIONS_REDUCTION_CLAIM.getMessage());
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
}
