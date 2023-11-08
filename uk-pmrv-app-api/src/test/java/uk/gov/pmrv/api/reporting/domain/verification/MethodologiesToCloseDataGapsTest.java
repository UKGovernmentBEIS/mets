package uk.gov.pmrv.api.reporting.domain.verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MethodologiesToCloseDataGapsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_data_gap_required_false_then_valid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_data_gap_required_false_then_invalid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.FALSE)
                .dataGapRequiredDetails(DataGapRequiredDetails.builder()
                        .dataGapApproved(Boolean.TRUE)
                        .build())
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gap_required_true_then_valid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.TRUE)
                .dataGapRequiredDetails(DataGapRequiredDetails.builder()
                        .dataGapApproved(Boolean.TRUE)
                        .build())
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_data_gap_required_true_then_invalid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.TRUE)
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gap_approved_false_then_valid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.TRUE)
                .dataGapRequiredDetails(DataGapRequiredDetails.builder()
                        .dataGapApproved(Boolean.FALSE)
                        .dataGapApprovedDetails(DataGapApprovedDetails.builder()
                                .conservativeMethodUsed(Boolean.TRUE)
                                .materialMisstatement(Boolean.FALSE)
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_conservative_method_true_then_invalid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.TRUE)
                .dataGapRequiredDetails(DataGapRequiredDetails.builder()
                        .dataGapApproved(Boolean.FALSE)
                        .dataGapApprovedDetails(DataGapApprovedDetails.builder()
                                .conservativeMethodUsed(Boolean.TRUE)
                                .methodDetails("method details")
                                .materialMisstatement(Boolean.FALSE)
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_conservative_method_false_then_valid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.TRUE)
                .dataGapRequiredDetails(DataGapRequiredDetails.builder()
                        .dataGapApproved(Boolean.FALSE)
                        .dataGapApprovedDetails(DataGapApprovedDetails.builder()
                                .conservativeMethodUsed(Boolean.FALSE)
                                .methodDetails("method details")
                                .materialMisstatement(Boolean.FALSE)
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_material_misstatement_true_then_valid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.TRUE)
                .dataGapRequiredDetails(DataGapRequiredDetails.builder()
                        .dataGapApproved(Boolean.FALSE)
                        .dataGapApprovedDetails(DataGapApprovedDetails.builder()
                                .conservativeMethodUsed(Boolean.FALSE)
                                .methodDetails("method details")
                                .materialMisstatement(Boolean.TRUE)
                                .materialMisstatementDetails("material misstatement details")
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_material_misstatement_false_then_invalid() {
        final MethodologiesToCloseDataGaps methodologiesToCloseDataGaps = MethodologiesToCloseDataGaps.builder()
                .dataGapRequired(Boolean.TRUE)
                .dataGapRequiredDetails(DataGapRequiredDetails.builder()
                        .dataGapApproved(Boolean.FALSE)
                        .dataGapApprovedDetails(DataGapApprovedDetails.builder()
                                .conservativeMethodUsed(Boolean.FALSE)
                                .methodDetails("method details")
                                .materialMisstatement(Boolean.FALSE)
                                .materialMisstatementDetails("material misstatement details")
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<MethodologiesToCloseDataGaps>> violations = validator.validate(methodologiesToCloseDataGaps);

        assertEquals(1, violations.size());
    }
}
