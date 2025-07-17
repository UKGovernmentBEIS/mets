package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CessationNotificationTest {

    private final Validator validator;

    public CessationNotificationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testInValidIfIsTemporaryNull() {
        CessationNotification cessationNotification = new CessationNotification();
        cessationNotification.setDescription("desc");
        cessationNotification.setDateOfNonCompliance(getDateOfNonCompliance());

        Set<ConstraintViolation<CessationNotification>> violations = validator.validate(cessationNotification);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void testValidIfIsTemporaryFalse() {
        CessationNotification cessationNotification = new CessationNotification();
        cessationNotification.setDescription("desc");
        cessationNotification.setIsTemporary(Boolean.FALSE);
        cessationNotification.setDateOfNonCompliance(getDateOfNonCompliance());

        Set<ConstraintViolation<CessationNotification>> violations = validator.validate(cessationNotification);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void testValidIfIsTemporaryTrueAndTechnicalCapabilityDetailsAreNull() {
        CessationNotification cessationNotification = new CessationNotification();
        cessationNotification.setDescription("desc");
        cessationNotification.setIsTemporary(Boolean.TRUE);
        cessationNotification.setDateOfNonCompliance(getDateOfNonCompliance());

        Set<ConstraintViolation<CessationNotification>> violations = validator.validate(cessationNotification);
        assertThat(violations.size()).isEqualTo(1);
    }

    DateOfNonCompliance getDateOfNonCompliance(){
        return DateOfNonCompliance.builder()
                .startDateOfNonCompliance(LocalDate.now())
                .endDateOfNonCompliance(LocalDate.now().plusDays(1))
                .build();
    }
}