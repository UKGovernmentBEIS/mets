package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DoalApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    @Builder.Default
    private SortedSet<PreliminaryAllocation> historicalPreliminaryAllocations = new TreeSet<>();

    @Builder.Default
    private List<HistoricalActivityLevel> historicalActivityLevels = new ArrayList<>();

    @Valid
    @NotNull
    private Doal doal;

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
        if(this.getDoal() != null) {
            final Set<UUID> operatorActivityLevelReportDocs =
                    this.getDoal().getOperatorActivityLevelReport() != null ?
                            Set.of(this.getDoal().getOperatorActivityLevelReport().getDocument()) : Set.of();
            final Set<UUID> verificationReportOfTheActivityLevelReportDocs =
                    this.getDoal().getVerificationReportOfTheActivityLevelReport() != null ?
                            Set.of(this.getDoal().getVerificationReportOfTheActivityLevelReport().getDocument()) : Set.of();
            final Set<UUID> additionalDocumentsDocs =
                    (this.getDoal().getAdditionalDocuments() != null) && (this.getDoal().getAdditionalDocuments().getDocuments() != null) ?
                           this.getDoal().getAdditionalDocuments().getDocuments() : Set.of();

            return Stream.of(operatorActivityLevelReportDocs, verificationReportOfTheActivityLevelReportDocs, additionalDocumentsDocs)
                    .flatMap(Set::stream).collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }
}
