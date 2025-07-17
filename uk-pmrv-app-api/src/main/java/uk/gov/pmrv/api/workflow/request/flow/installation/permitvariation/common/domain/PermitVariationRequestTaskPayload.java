package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermitVariationRequestTaskPayload extends RequestTaskPayload {

	private PermitType permitType;

	private Permit permit;

	private InstallationOperatorDetails installationOperatorDetails;
	
	private PermitVariationDetails permitVariationDetails;
	
	private Boolean permitVariationDetailsCompleted;

	@Builder.Default
	private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();

	@Builder.Default
	private Map<UUID, String> permitAttachments = new HashMap<>();
	
	@Override
	public Map<UUID, String> getAttachments() {
		return getPermitAttachments();
	}

	@Override
	public Set<UUID> getReferencedAttachmentIds() {
		return getPermit() != null ? getPermit().getPermitSectionAttachmentIds() : Collections.emptySet();
	}
	
}
