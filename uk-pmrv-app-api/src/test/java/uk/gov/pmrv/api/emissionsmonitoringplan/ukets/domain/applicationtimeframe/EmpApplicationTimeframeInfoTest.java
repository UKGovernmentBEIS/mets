package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmpApplicationTimeframeInfoTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_application_timeframe_info_valid() {
        final EmpApplicationTimeframeInfo applicationTimeFrame1 = EmpApplicationTimeframeInfo.builder()
                .dateOfStart(LocalDate.ofYearDay(2023, 1))
                .submittedOnTime(true)
                .build();

        final Set<ConstraintViolation<EmpApplicationTimeframeInfo>> violations1 = validator.validate(applicationTimeFrame1);
        assertEquals(0, violations1.size());

        final EmpApplicationTimeframeInfo applicationTimeFrame2 = EmpApplicationTimeframeInfo.builder()
                .dateOfStart(LocalDate.ofYearDay(2023, 1))
                .submittedOnTime(false)
                .reasonForLateSubmission("My reason")
                .build();

        final Set<ConstraintViolation<EmpApplicationTimeframeInfo>> violations2 = validator.validate(applicationTimeFrame2);
        assertEquals(0, violations2.size());
    }

    @Test
    void when_application_timeframe_info_invalid() {
        final EmpApplicationTimeframeInfo applicationTimeFrame1 = EmpApplicationTimeframeInfo.builder()
                .dateOfStart(LocalDate.ofYearDay(2023, 1))
                .submittedOnTime(false)
                .build();

        final Set<ConstraintViolation<EmpApplicationTimeframeInfo>> violations1 = validator.validate(applicationTimeFrame1);
        assertEquals(1, violations1.size()); // no reason provided

        final EmpApplicationTimeframeInfo applicationTimeFrame2 = EmpApplicationTimeframeInfo.builder()
                .submittedOnTime(false)
                .reasonForLateSubmission("a reason")
                .build();

        final Set<ConstraintViolation<EmpApplicationTimeframeInfo>> violations2 = validator.validate(applicationTimeFrame2);
        assertEquals(1, violations2.size()); // no date provided
    }

}
