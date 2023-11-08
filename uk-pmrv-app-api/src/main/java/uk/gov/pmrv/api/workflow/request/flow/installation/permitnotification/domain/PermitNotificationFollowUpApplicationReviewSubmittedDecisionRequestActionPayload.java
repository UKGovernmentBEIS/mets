package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload extends RequestActionPayload {
    
    @NotNull
    private PermitNotificationType permitNotificationType;
    
    @NotBlank
    @Size(max = 10000)
    private String request;
    
    @NotNull
    private LocalDate responseExpirationDate;

    @NotBlank
    @Size(max = 10000)
    private String response;

    @Builder.Default
    private Set<UUID> responseFiles = new HashSet<>();

    @Builder.Default
    private Map<UUID, String> responseAttachments = new HashMap<>();

    @NotNull
    @PastOrPresent
    private LocalDate responseSubmissionDate;
    
    @NotNull
    private PermitNotificationFollowUpReviewDecision reviewDecision;

    @NotNull
    private DecisionNotification reviewDecisionNotification;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.responseAttachments;
    }
}
