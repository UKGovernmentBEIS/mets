package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;

class PermitBatchReissueFiltersTest {

	private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
	
	@Test
	void emitterTypeHseOnlyAndInstallationCategoryNotNull_should_be_invalid() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.HSE))
				.installationCategories(Set.of(InstallationCategory.A))
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.emitterTypeAndInstallationCategory}");
	}
	
	@Test
	void emitterTypeHseOnlyAndInstallationCategoryNull_should_be_valid() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.HSE))
				.installationCategories(null)
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).isEmpty();
	}
	
	@Test
	void emitterTypeHseAndGHGEAndInstallationCategoryNotNull_should_be_valid() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.HSE, EmitterType.GHGE))
				.installationCategories(Set.of(InstallationCategory.A))
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).isEmpty();
	}
	
	@Test
	void emitterTypeHseAndGHGEAndInstallationCategoryNull_should_be_invalid() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.HSE, EmitterType.GHGE))
				.installationCategories(null)
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.emitterTypeAndInstallationCategory}");
	}
	
	@Test
	void emitterTypeHseAndGHGEAndInstallationCategoryEmpty_should_be_invalid() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.HSE, EmitterType.GHGE))
				.installationCategories(Set.of())
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.emitterTypeAndInstallationCategory}");
	}

	@Test
	void emitterTypeWasteAndGHGEAndInstallationCategoryEmpty_should_be_invalid() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.WASTE, EmitterType.GHGE))
				.installationCategories(Set.of())
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.emitterTypeAndInstallationCategory}");
	}

	@Test
	void installationCategoryNA_should_be_invalid() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.HSE, EmitterType.GHGE))
				.installationCategories(Set.of(InstallationCategory.N_A))
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.installationCategory.notAvailable}");
	}

	@Test
	void installationCategoryNA_should_be_invalid_forWaste() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.WASTE, EmitterType.GHGE))
				.installationCategories(Set.of(InstallationCategory.N_A))
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.installationCategory.notAvailable}");
	}

	@Test
	void installationCategoryNA_should_be_invalid_forWasteOnly() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.WASTE))
				.installationCategories(Set.of(InstallationCategory.N_A))
				.build();
		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.installationCategory.notAvailable}");
	}


	@Test
	void validNonGHGEWithoutInstallations() {
		PermitBatchReissueFilters payload = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.emitterTypes(Set.of(EmitterType.WASTE))
				.installationCategories(null)
				.build();

		Set<ConstraintViolation<PermitBatchReissueFilters>> violations = validator.validate(payload);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{permit.reissue.batch.create.emitterTypeAndInstallationCategory}");

	}
}
