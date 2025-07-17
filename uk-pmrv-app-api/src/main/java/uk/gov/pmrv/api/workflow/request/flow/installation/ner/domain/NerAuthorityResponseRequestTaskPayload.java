package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.LocalDate;
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
public class NerAuthorityResponseRequestTaskPayload extends RequestTaskPayload {

    @Builder.Default
    private SortedSet<@Valid @NotNull PreliminaryAllocation> originalPreliminaryAllocations = new TreeSet<>();

    @NotNull
    @PastOrPresent
    private LocalDate submittedToAuthorityDate;

    @NotNull
    @Valid
    private AuthorityResponse authorityResponse;

    @Builder.Default
    private Map<String, Boolean> nerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> nerAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNerAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return authorityResponse != null ? authorityResponse.getFiles() : Set.of();
    }
}
