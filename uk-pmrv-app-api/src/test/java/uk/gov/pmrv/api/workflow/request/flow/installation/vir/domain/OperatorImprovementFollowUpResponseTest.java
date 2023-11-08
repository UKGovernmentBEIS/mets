package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OperatorImprovementFollowUpResponseTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void OperatorImprovementFollowUpResponse_with_no_improvement() {
        final OperatorImprovementFollowUpResponse operatorImprovement = OperatorImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .reason("Reason")
                .build();

        final Set<ConstraintViolation<OperatorImprovementFollowUpResponse>> violations =
                validator.validate(operatorImprovement);

        assertEquals(0, violations.size());
    }

    @Test
    void OperatorImprovementFollowUpResponse_with_improvement() {
        final OperatorImprovementFollowUpResponse operatorImprovement = OperatorImprovementFollowUpResponse.builder()
                .improvementCompleted(true)
                .dateCompleted(LocalDate.now())
                .build();

        final Set<ConstraintViolation<OperatorImprovementFollowUpResponse>> violations =
                validator.validate(operatorImprovement);

        assertEquals(0, violations.size());
    }

    @Test
    void OperatorImprovementFollowUpResponse_with_no_improvement_not_valid() {
        final OperatorImprovementFollowUpResponse operatorImprovement = OperatorImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .build();

        final Set<ConstraintViolation<OperatorImprovementFollowUpResponse>> violations =
                validator.validate(operatorImprovement);

        assertEquals(1, violations.size());
    }

    @Test
    void OperatorImprovementFollowUpResponse_with_improvement_not_valid() {
        final OperatorImprovementFollowUpResponse operatorImprovement = OperatorImprovementFollowUpResponse.builder()
                .improvementCompleted(true)
                .build();

        final Set<ConstraintViolation<OperatorImprovementFollowUpResponse>> violations =
                validator.validate(operatorImprovement);

        assertEquals(1, violations.size());
    }
}
