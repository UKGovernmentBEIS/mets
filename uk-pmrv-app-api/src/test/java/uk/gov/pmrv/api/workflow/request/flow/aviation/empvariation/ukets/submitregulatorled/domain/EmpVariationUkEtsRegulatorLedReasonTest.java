package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class EmpVariationUkEtsRegulatorLedReasonTest {

	private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
	
	@Test
	void isValid_not_other_selected() {
		final EmpVariationUkEtsRegulatorLedReason reason = EmpVariationUkEtsRegulatorLedReason.builder()
				.type(EmpVariationUkEtsRegulatorLedReasonType.FAILED_TO_COMPLY_OR_APPLY)
				.build();
		
		Set<ConstraintViolation<EmpVariationUkEtsRegulatorLedReason>> violations = validator.validate(reason);
		assertThat(violations).isEmpty();
	}
	
	@Test
	void isValid_other_selected_without_other_reason() {
		final EmpVariationUkEtsRegulatorLedReason reason = EmpVariationUkEtsRegulatorLedReason.builder()
				.type(EmpVariationUkEtsRegulatorLedReasonType.OTHER)
				.build();
		
		Set<ConstraintViolation<EmpVariationUkEtsRegulatorLedReason>> violations = validator.validate(reason);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{empvariation.ukets.submitregulatorled.reason.typeOtherSummary}");
	}
	
	@Test
	void isValid_other_selected_with_other_reason() {
		final EmpVariationUkEtsRegulatorLedReason reason = EmpVariationUkEtsRegulatorLedReason.builder()
				.type(EmpVariationUkEtsRegulatorLedReasonType.OTHER)
				.reasonOtherSummary("Sdsd")
				.build();
		
		Set<ConstraintViolation<EmpVariationUkEtsRegulatorLedReason>> violations = validator.validate(reason);
		assertThat(violations).isEmpty();
	}
	
}
