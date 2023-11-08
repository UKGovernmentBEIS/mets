package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirExpirable;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload extends
    AviationVirApplicationRequestTaskPayload implements VirExpirable {

    @Builder.Default
    private Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = new HashMap<>();

    @Builder.Default
    private Map<String, OperatorImprovementFollowUpResponse> operatorImprovementFollowUpResponses = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> virRespondToRegulatorCommentsSectionsCompleted = new HashMap<>();
}
