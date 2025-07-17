package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DateOfNonComplianceTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void validModel() {
        DateOfNonCompliance dateOfNonCompliance = DateOfNonCompliance.builder()
            .startDateOfNonCompliance(LocalDate.now())
            .endDateOfNonCompliance(LocalDate.now().plusDays(1))
            .build();

        Set<ConstraintViolation<DateOfNonCompliance>> violations = validator.validate(dateOfNonCompliance);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void invalidModelDueToEndDateBeforeStartDate() {
        DateOfNonCompliance dateOfNonCompliance = DateOfNonCompliance.builder()
            .startDateOfNonCompliance(LocalDate.now().plusDays(5))
            .endDateOfNonCompliance(LocalDate.now().plusDays(1))
            .build();

        Set<ConstraintViolation<DateOfNonCompliance>> violations = validator.validate(dateOfNonCompliance);

        assertThat(violations.size()).isEqualTo(1);
    }
}
