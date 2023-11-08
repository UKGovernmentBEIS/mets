package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermitVariationRegulatorLedGrantDeterminationTest {

	private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
	
	@Test
	void isValid_other_selected() {
		final PermitVariationRegulatorLedGrantDetermination determination = PermitVariationRegulatorLedGrantDetermination.builder()
				.activationDate(LocalDate.of(LocalDate.now().getYear() + 1, 1, 30))
				.reason("reason")
				.logChanges("logChanges")
				.reasonTemplate(PermitVariationReasonTemplate.OTHER)
				.build();
		
		Set<ConstraintViolation<PermitVariationRegulatorLedGrantDetermination>> violations = validator.validate(determination);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permitvariation.regulatorled.grantdetermination.reasontemplate.typeOtherSummary}");
	}
	
	@Test
	void isValid_other_selected_with_summary() {
		final PermitVariationRegulatorLedGrantDetermination determination = PermitVariationRegulatorLedGrantDetermination.builder()
				.activationDate(LocalDate.of(LocalDate.now().getYear() + 1, 1, 30))
				.reason("reason")
				.logChanges("logChanges")
				.reasonTemplate(PermitVariationReasonTemplate.OTHER)
				.reasonTemplateOtherSummary("other")
				.build();
		
		Set<ConstraintViolation<PermitVariationRegulatorLedGrantDetermination>> violations = validator.validate(determination);
		assertThat(violations).hasSize(0);
	}
}
