package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

@ExtendWith(MockitoExtension.class)
class RegulatorImprovementResponseTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void regulatorImprovementResponse_with_date_valid() {
        final RegulatorImprovementResponse regulatorImprovementResponse = RegulatorImprovementResponse.builder()
                .improvementRequired(true)
                .improvementDeadline(LocalDate.now())
                .operatorActions("action to be taken")
                .build();

        final Set<ConstraintViolation<RegulatorImprovementResponse>> violations =
                validator.validate(regulatorImprovementResponse);

        assertEquals(0, violations.size());
    }

    @Test
    void regulatorImprovementResponse_valid() {
        final RegulatorImprovementResponse regulatorImprovementResponse = RegulatorImprovementResponse.builder()
                .improvementRequired(false)
                .operatorActions("action to be taken")
                .build();

        final Set<ConstraintViolation<RegulatorImprovementResponse>> violations =
                validator.validate(regulatorImprovementResponse);

        assertEquals(0, violations.size());
    }

    @Test
    void regulatorImprovementResponse_with_date_not_valid() {
        final RegulatorImprovementResponse regulatorImprovementResponse = RegulatorImprovementResponse.builder()
                .improvementRequired(false)
                .improvementDeadline(LocalDate.now())
                .operatorActions("action to be taken")
                .build();

        final Set<ConstraintViolation<RegulatorImprovementResponse>> violations =
                validator.validate(regulatorImprovementResponse);

        assertEquals(1, violations.size());
    }

    @Test
    void regulatorImprovementResponse_not_valid() {
        final RegulatorImprovementResponse regulatorImprovementResponse = RegulatorImprovementResponse.builder()
                .improvementRequired(true)
                .operatorActions("action to be taken")
                .build();

        final Set<ConstraintViolation<RegulatorImprovementResponse>> violations =
                validator.validate(regulatorImprovementResponse);

        assertEquals(1, violations.size());
    }
}
