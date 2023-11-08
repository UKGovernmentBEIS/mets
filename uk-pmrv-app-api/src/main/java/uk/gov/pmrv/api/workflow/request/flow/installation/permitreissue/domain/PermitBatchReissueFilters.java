package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueFilters;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#emitterTypes.contains('GHGE') && #installationCategories != null && #installationCategories.size() > 0) || (!#emitterTypes.contains('GHGE') && (#installationCategories == null || #installationCategories.isEmpty()))}", 
	message = "permit.reissue.batch.create.emitterTypeAndInstallationCategory")
@SpELExpression(expression = "{#installationCategories == null || !#installationCategories.contains('N_A')}", 
	message = "permit.reissue.batch.create.installationCategory.notAvailable")
public class PermitBatchReissueFilters extends BatchReissueFilters {

	@NotEmpty
	@Builder.Default
	private Set<InstallationAccountStatus> accountStatuses = new HashSet<>();
	
	@NotEmpty
	@Builder.Default
	private Set<EmitterType> emitterTypes = new HashSet<>();
	
	private Set<InstallationCategory> installationCategories;
	
}
