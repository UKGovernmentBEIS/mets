package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NerApplicationProceededToAuthorityRequestActionPayload extends RequestActionPayload {

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
    private Map<NerReviewGroup, NerReviewGroupDecision> reviewGroupDecisions = new EnumMap<>(NerReviewGroup.class);
    
    @Valid
    @NotNull
    private NerProceedToAuthorityDetermination determination;
    
    @Valid
    private DecisionNotification decisionNotification;

    @Builder.Default
    private Map<UUID, String> nerAttachments = new HashMap<>();
    
    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNerAttachments();
    }
}
