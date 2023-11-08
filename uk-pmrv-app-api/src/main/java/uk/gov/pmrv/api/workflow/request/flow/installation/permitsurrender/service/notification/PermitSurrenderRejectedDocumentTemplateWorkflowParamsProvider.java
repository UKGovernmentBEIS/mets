package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification;

import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;

@Component
public class PermitSurrenderRejectedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitSurrenderRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_REJECTED;
    }
    
    @Override
    public Map<String, Object> constructParams(PermitSurrenderRequestPayload payload, String requestId) {
        PermitSurrenderReviewDeterminationReject determiation = (PermitSurrenderReviewDeterminationReject) payload.getReviewDetermination();
        return Map.of(
                "officialRefusalLetter", determiation.getOfficialRefusalLetter(),
                "shouldFeeBeRefundedToOperator", determiation.getShouldFeeBeRefundedToOperator()
                );
    }

}

