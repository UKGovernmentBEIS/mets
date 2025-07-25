package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PermitSurrenderApplicationSubmitRequestTaskPayload extends RequestTaskPayload {
    
    @Valid
    @NotNull
    private PermitSurrender permitSurrender;
    
    @Builder.Default
    private Map<UUID, String> permitSurrenderAttachments = new HashMap<>();
    
    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
    
    @Override
    public Map<UUID, String> getAttachments() {
        return getPermitSurrenderAttachments();
    }
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getPermitSurrender() != null ?
                getPermitSurrender().getAttachmentIds() :
            Collections.emptySet();
    }

}
