package uk.gov.pmrv.api.reporting.domain.verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OverallAssessmentTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void verified_with_comments_valid() {
        final VerifiedWithCommentsOverallAssessment verifiedWithCommentsOverallAssessment =
                VerifiedWithCommentsOverallAssessment.builder()
                        .type(OverallAssessmentType.VERIFIED_WITH_COMMENTS)
                        .reasons(List.of(
                                "Reason 1",
                                "Reason 2"
                        ))
                        .build();

        final Set<ConstraintViolation<VerifiedWithCommentsOverallAssessment>> violations =
                validator.validate(verifiedWithCommentsOverallAssessment);

        assertEquals(0, violations.size());
    }

    @Test
    void verified_with_comments_when_empty_reasons_invalid() {
        final VerifiedWithCommentsOverallAssessment verifiedWithCommentsOverallAssessment =
                VerifiedWithCommentsOverallAssessment.builder()
                        .type(OverallAssessmentType.VERIFIED_WITH_COMMENTS)
                        .reasons(List.of())
                        .build();

        final Set<ConstraintViolation<VerifiedWithCommentsOverallAssessment>> violations =
                validator.validate(verifiedWithCommentsOverallAssessment);

        assertEquals(1, violations.size());
    }

    @Test
    void verified_with_comments_when_blank_reasons_invalid() {
        final VerifiedWithCommentsOverallAssessment verifiedWithCommentsOverallAssessment =
                VerifiedWithCommentsOverallAssessment.builder()
                        .type(OverallAssessmentType.VERIFIED_WITH_COMMENTS)
                        .reasons(List.of(
                                "Reason 1",
                                ""
                        ))
                        .build();

        final Set<ConstraintViolation<VerifiedWithCommentsOverallAssessment>> violations =
                validator.validate(verifiedWithCommentsOverallAssessment);

        assertEquals(1, violations.size());
    }

    @Test
    void not_verified_valid() {
        final NotVerifiedOverallAssessment notVerifiedOverallAssessment =
                NotVerifiedOverallAssessment.builder()
                        .type(OverallAssessmentType.NOT_VERIFIED)
                        .notVerifiedReasons(List.of(
                                NotVerifiedReason.builder()
                                        .type(NotOverallVerificationReasonType.NOT_APPROVED_MONITORING_PLAN)
                                        .build()
                        ))
                        .build();

        final Set<ConstraintViolation<NotVerifiedOverallAssessment>> violations =
                validator.validate(notVerifiedOverallAssessment);

        assertEquals(0, violations.size());
    }

    @Test
    void not_verified_another_reason_valid() {
        final NotVerifiedOverallAssessment notVerifiedOverallAssessment =
                NotVerifiedOverallAssessment.builder()
                        .type(OverallAssessmentType.NOT_VERIFIED)
                        .notVerifiedReasons(List.of(
                                NotVerifiedReason.builder()
                                        .type(NotOverallVerificationReasonType.NOT_APPROVED_MONITORING_PLAN)
                                        .build(),
                                NotVerifiedReason.builder()
                                        .type(NotOverallVerificationReasonType.ANOTHER_REASON)
                                        .otherReason("Another reason")
                                        .build()
                        ))
                        .build();

        final Set<ConstraintViolation<NotVerifiedOverallAssessment>> violations =
                validator.validate(notVerifiedOverallAssessment);

        assertEquals(0, violations.size());
    }

    @Test
    void not_verified_another_reason_invalid() {
        final NotVerifiedOverallAssessment notVerifiedOverallAssessment =
                NotVerifiedOverallAssessment.builder()
                        .type(OverallAssessmentType.NOT_VERIFIED)
                        .notVerifiedReasons(List.of(
                                NotVerifiedReason.builder()
                                        .type(NotOverallVerificationReasonType.ANOTHER_REASON)
                                        .build()
                        ))
                        .build();

        final Set<ConstraintViolation<NotVerifiedOverallAssessment>> violations =
                validator.validate(notVerifiedOverallAssessment);

        assertEquals(1, violations.size());
    }

    @Test
    void not_verified_no_reasons_invalid() {
        final NotVerifiedOverallAssessment notVerifiedOverallAssessment =
                NotVerifiedOverallAssessment.builder()
                        .type(OverallAssessmentType.NOT_VERIFIED)
                        .notVerifiedReasons(List.of())
                        .build();

        final Set<ConstraintViolation<NotVerifiedOverallAssessment>> violations =
                validator.validate(notVerifiedOverallAssessment);

        assertEquals(1, violations.size());
    }
}
