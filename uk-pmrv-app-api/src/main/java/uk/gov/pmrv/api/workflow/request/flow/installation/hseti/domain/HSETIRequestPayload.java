package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HSETIRequestPayload extends RequestPayload implements RequestPayloadPayable {

    private HSETI hseti;

    @Builder.Default
    private Map<UUID, String> hsetiAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> hsetiSectionsCompleted = new HashMap<>();

    private DecisionNotification decisionNotification;

    private RequestPaymentInfo requestPaymentInfo;

    @NotNull
    private HSETIRegulatorReviewOverallDecision overallDecision;

    @Builder.Default
    private Map<HSETIReviewGroup, HSETIRegulatorReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(HSETIReviewGroup.class);

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    private FileInfoDTO officialNotice;
}
