package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.EnumMap;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class NerRequestPayload extends RequestPayload implements RequestPayloadPayable {

    private NER ner;

    private RequestPaymentInfo requestPaymentInfo;

    @Builder.Default
    private int nerFileVersion = 1;

    private boolean verificationPerformed;

    private NERVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> nerAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> nerSectionsCompleted = new HashMap<>();

    private NERApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<NERReviewGroup, NERReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(NERReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    public void incrementNerFileVersion() {
        this.nerFileVersion += 1;
    }

    @JsonIgnore
    public NERVerificationData getVerificationData() {
        return verificationReport == null ? null : verificationReport.getVerificationData();
    }
}
