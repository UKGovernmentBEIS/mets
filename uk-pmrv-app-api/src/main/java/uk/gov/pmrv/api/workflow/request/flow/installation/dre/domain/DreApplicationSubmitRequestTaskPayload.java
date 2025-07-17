package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DreApplicationSubmitRequestTaskPayload extends RequestTaskPayload {
	
	private Dre dre;
	
	private boolean sectionCompleted;
	
	@Builder.Default
    private Map<UUID, String> dreAttachments = new HashMap<>();
	
	@Override
	public Map<UUID, String> getAttachments() {
		return dreAttachments;
	}
	
	@Override
    public Set<UUID> getReferencedAttachmentIds() {
		final Set<UUID> supportingDocuments = (dre != null && dre.getDeterminationReason() != null)
				? dre.getDeterminationReason().getSupportingDocuments()
				: Collections.emptySet();
		
		return Stream.of(super.getReferencedAttachmentIds(), supportingDocuments)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
    }
}
