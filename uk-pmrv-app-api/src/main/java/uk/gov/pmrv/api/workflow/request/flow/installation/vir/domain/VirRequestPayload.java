package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VirRequestPayload extends RequestPayload implements RequestPayloadRfiable {

    private VirVerificationData verificationData;

    @Builder.Default
    private Map<String, OperatorImprovementResponse> operatorImprovementResponses = new HashMap<>();

    private RegulatorReviewResponse regulatorReviewResponse;

    private DecisionNotification decisionNotification;

    @Builder.Default
    private Map<String, Boolean> virSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> virAttachments = new HashMap<>();

    private FileInfoDTO officialNotice;

    @JsonUnwrapped
    private RfiData rfiData;
}
