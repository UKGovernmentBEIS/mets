package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Collections;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ALRAuthorityResponseSubmitRequestTaskPayload extends RequestTaskPayload {

    // Allocations submitted by regulator that can't be changed from UK Authority (treat like historical)
    @Builder.Default
    private Set<ALRPreliminaryAllocation> regulatorPreliminaryAllocations = new TreeSet<>();

    @NotNull
    private ALRApplicationAuthorityReviewOutcome authorityReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> authorityReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> alrAttachments = new HashMap<>();

    private int alrFileVersion;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getAlrAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        if(this.getAuthorityReviewOutcome() != null && this.getAuthorityReviewOutcome().getAuthorityResponse() !=null
                && (this.getAuthorityReviewOutcome().getAuthorityResponse().getType().equals(DoalAuthorityResponseType.VALID) ||
                this.getAuthorityReviewOutcome().getAuthorityResponse().getType().equals(DoalAuthorityResponseType.VALID_WITH_CORRECTIONS))) {
            Set<UUID> documents = ((ALRGrantAuthorityResponse) this.getAuthorityReviewOutcome().getAuthorityResponse()).getDocuments();
            return documents != null ? documents : Collections.emptySet();
        }

        return Collections.emptySet();
    }
}
