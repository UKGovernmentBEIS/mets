package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @NotNull
    private UUID noticeOfIntent;
    
    @Size(max = 10000)
    private String comments;

    @NotNull
    @JsonUnwrapped
    private NonComplianceDecisionNotification decisionNotification;
    
    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNonComplianceAttachments();
    }
}
