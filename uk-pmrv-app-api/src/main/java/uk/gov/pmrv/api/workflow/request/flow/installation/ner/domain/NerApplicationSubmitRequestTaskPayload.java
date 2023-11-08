package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NerApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    @JsonUnwrapped
    @Valid
    @NotNull
    private NerOperatorDocuments nerOperatorDocuments;

    @Valid
    @NotNull
    private ConfidentialityStatement confidentialityStatement;

    @Valid
    @NotNull
    private AdditionalDocuments additionalDocuments;

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

        final Set<UUID> additionalDocs =
            this.getAdditionalDocuments() != null && this.getAdditionalDocuments().getDocuments() != null ?
                this.getAdditionalDocuments().getDocuments() : Set.of();

        final Set<UUID> operatorDocuments = this.getNerOperatorDocuments() != null ? 
            this.getNerOperatorDocuments().getDocuments() : Set.of();
        
        return Stream.concat(additionalDocs.stream(), operatorDocuments.stream()).collect(Collectors.toSet());

    }
}
