package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AirRequestPayload extends RequestPayload implements RequestPayloadRfiable {

    @Builder.Default
    private Map<Integer, AirImprovement> airImprovements = new HashMap<>();

    @Builder.Default
    private Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> airAttachments = new HashMap<>();

    @Builder.Default
    private Map<Integer, Boolean> airSectionsCompleted = new HashMap<>();
    
    private RegulatorAirReviewResponse regulatorReviewResponse;

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    private DecisionNotification decisionNotification;
    
    @JsonUnwrapped
    private RfiData rfiData;

    private FileInfoDTO officialNotice;
}
