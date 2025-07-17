package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class NerRequestPayload extends RequestPayload implements RequestPayloadPayable {

    @JsonUnwrapped
    private NerOperatorDocuments nerOperatorDocuments;

    private ConfidentialityStatement confidentialityStatement;

    private AdditionalDocuments additionalDocuments;
    
    private NerDetermination determination;

    @Builder.Default
    private Map<NerReviewGroup, NerReviewGroupDecision> reviewGroupDecisions = new EnumMap<>(NerReviewGroup.class);

    private RequestPaymentInfo requestPaymentInfo;

    private DecisionNotification decisionNotification;

    private LocalDate submittedToAuthorityDate;

    private AuthorityResponse authorityResponse;

    @Builder.Default
    private Map<UUID, String> nerAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> nerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
