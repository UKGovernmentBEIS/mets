package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TemporarySuspensionTest {

    private final Validator validator;

    public TemporarySuspensionTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testMandatoryFields() {
        TemporarySuspension temporarySuspension = new TemporarySuspension();
        temporarySuspension.setDescription("desc");
        temporarySuspension.setDateOfNonCompliance(getDateOfNonCompliance());

        Set<ConstraintViolation<TemporarySuspension>> violations = validator.validate(temporarySuspension);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void testMissingMandatoryField() {
        TemporarySuspension temporarySuspension = new TemporarySuspension();
        temporarySuspension.setDescription(null);
        temporarySuspension.setDateOfNonCompliance(getMissingDateOfNonCompliance());

        Set<ConstraintViolation<TemporarySuspension>> violations = validator.validate(temporarySuspension);
        assertThat(violations.size()).isEqualTo(2);
    }


    DateOfNonCompliance getDateOfNonCompliance(){
        return DateOfNonCompliance.builder()
                .startDateOfNonCompliance(LocalDate.now())
                .endDateOfNonCompliance(LocalDate.now().plusDays(1))
                .build();
    }

    DateOfNonCompliance getMissingDateOfNonCompliance(){
        return DateOfNonCompliance.builder()
                .startDateOfNonCompliance(null)
                .endDateOfNonCompliance(null)
                .build();
    }
}