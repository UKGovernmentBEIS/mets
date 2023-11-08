package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AviationDreFeeTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_when_no_charge_valid() {
        AviationDreFee fee = AviationDreFee.builder()
            .chargeOperator(false)
            .build();

        Set<ConstraintViolation<AviationDreFee>> violations = validator.validate(fee);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_when_no_charge_and_details_exist_invalid() {
        AviationDreFee fee = AviationDreFee.builder()
            .chargeOperator(false)
            .feeDetails(AviationDreFeeDetails.builder()
                .totalBillableHours(BigDecimal.valueOf(3.5))
                .hourlyRate(BigDecimal.valueOf(2.25))
                .dueDate(LocalDate.now().plusDays(1))
                .build())
            .build();

        Set<ConstraintViolation<AviationDreFee>> violations = validator.validate(fee);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_when_charge_valid() {
        AviationDreFee fee = AviationDreFee.builder()
            .chargeOperator(true)
            .feeDetails(AviationDreFeeDetails.builder()
                .totalBillableHours(BigDecimal.valueOf(3.5))
                .hourlyRate(BigDecimal.valueOf(2.25))
                .dueDate(LocalDate.now().plusDays(1))
                .build())
            .build();

        Set<ConstraintViolation<AviationDreFee>> violations = validator.validate(fee);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_when_charge_and_invalid_details_invalid() {
        AviationDreFee fee = AviationDreFee.builder()
            .chargeOperator(true)
            .feeDetails(AviationDreFeeDetails.builder()
                .totalBillableHours(BigDecimal.valueOf(3.505))
                .dueDate(LocalDate.now().minusDays(1))
                .build())
            .build();

        Set<ConstraintViolation<AviationDreFee>> violations = validator.validate(fee);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void validate_when_charge_and_no_details_invalid() {
        AviationDreFee fee = AviationDreFee.builder()
            .chargeOperator(true)
            .build();

        Set<ConstraintViolation<AviationDreFee>> violations = validator.validate(fee);

        assertEquals(1, violations.size());
    }

}