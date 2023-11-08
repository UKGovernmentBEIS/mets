package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OperatorImprovementResponseTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void operatorImprovementResponse_with_date_files_valid() {
        final OperatorImprovementResponse operatorImprovementResponse = OperatorImprovementResponse.builder()
                .isAddressed(true)
                .addressedDescription("Description")
                .addressedDate(LocalDate.now())
                .uploadEvidence(true)
                .files(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<OperatorImprovementResponse>> violations =
                validator.validate(operatorImprovementResponse);

        assertEquals(0, violations.size());
    }

    @Test
    void operatorImprovementResponse_with_no_date_not_valid() {
        final OperatorImprovementResponse operatorImprovementResponse = OperatorImprovementResponse.builder()
                .isAddressed(true)
                .addressedDescription("Description")
                .uploadEvidence(true)
                .files(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<OperatorImprovementResponse>> violations =
                validator.validate(operatorImprovementResponse);

        assertEquals(1, violations.size());
    }

    @Test
    void operatorImprovementResponse_with_no_files_not_valid() {
        final OperatorImprovementResponse operatorImprovementResponse = OperatorImprovementResponse.builder()
                .isAddressed(true)
                .addressedDescription("Description")
                .addressedDate(LocalDate.now())
                .uploadEvidence(true)
                .build();

        final Set<ConstraintViolation<OperatorImprovementResponse>> violations =
                validator.validate(operatorImprovementResponse);

        assertEquals(1, violations.size());
    }

    @Test
    void operatorImprovementResponse_with_date_files_not_valid() {
        final OperatorImprovementResponse operatorImprovementResponse = OperatorImprovementResponse.builder()
                .isAddressed(false)
                .addressedDescription("Description")
                .addressedDate(LocalDate.now())
                .uploadEvidence(false)
                .files(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<OperatorImprovementResponse>> violations =
                validator.validate(operatorImprovementResponse);

        assertEquals(2, violations.size());
    }
}
