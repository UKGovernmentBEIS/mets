package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitTransferAApplicationRequestTaskPayload extends RequestTaskPayload {

    @NotNull
    @Valid
    @JsonUnwrapped
    private PermitTransferDetails permitTransferDetails;
    
    private Boolean sectionCompleted;

    @Builder.Default
    private Map<UUID, String> transferAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getTransferAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.getPermitTransferDetails() != null ? 
            this.getPermitTransferDetails().getReasonAttachments() : Set.of();
    }
}
