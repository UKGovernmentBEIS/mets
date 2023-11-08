package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
public class NonComplianceFinalDeterminationRequestTaskPayload extends RequestTaskPayload implements NonComplianceRequestTaskClosable {

    @NotNull
    @Valid
    @JsonUnwrapped
    private NonComplianceFinalDetermination finalDetermination;

    private NonComplianceCloseJustification closeJustification;

    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    private Boolean determinationCompleted;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNonComplianceAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.closeJustification != null ? this.closeJustification.getFiles() : Set.of();
    }
}
