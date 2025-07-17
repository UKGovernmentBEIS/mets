package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NerApplicationEndedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private NerEndedDetermination determination;
    
    @Valid
    @NotNull
    private DecisionNotification decisionNotification;
    
    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();
}
