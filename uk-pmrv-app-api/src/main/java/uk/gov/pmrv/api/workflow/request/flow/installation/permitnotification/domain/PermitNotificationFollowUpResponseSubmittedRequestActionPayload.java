package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpResponseSubmittedRequestActionPayload extends RequestActionPayload {
    
    @NotBlank
    @Size(max = 10000)
    private String request;
    
    @NotBlank
    @Size(max = 10000)
    private String response;

    @Builder.Default
    private Set<UUID> responseFiles = new HashSet<>();

    @Builder.Default
    private Map<UUID, String> responseAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.responseAttachments;
    }
}
