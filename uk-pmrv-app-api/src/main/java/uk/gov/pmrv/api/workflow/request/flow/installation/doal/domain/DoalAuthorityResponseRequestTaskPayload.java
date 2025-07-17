package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DoalAuthorityResponseRequestTaskPayload extends RequestTaskPayload {

    // Allocations submitted by regulator that can't be changed from UK Authority (treat like historical)
    @Builder.Default
    private SortedSet<PreliminaryAllocation> regulatorPreliminaryAllocations = new TreeSet<>();

    @Valid
    @NotNull
    private DoalAuthority doalAuthority;

    @Builder.Default
    private Map<String, Boolean> doalSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> doalAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getDoalAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        if(this.getDoalAuthority() != null && this.getDoalAuthority().getAuthorityResponse() !=null
                && this.getDoalAuthority().getAuthorityResponse().getType().equals(DoalAuthorityResponseType.VALID)) {
            Set<UUID> documents = ((DoalGrantAuthorityResponse) this.getDoalAuthority().getAuthorityResponse()).getDocuments();
            return documents != null ? documents : Collections.emptySet();
        }

        return Collections.emptySet();
    }
}
