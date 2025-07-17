package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

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
public class PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotBlank
    @Size(max = 10000)
    private String response;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

    @Builder.Default
    private Map<String, Boolean> followUpSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
