package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;

import java.util.Map;

@Component
public class VirReviewedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<VirRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.VIR_REVIEWED;
    }

    @Override
    public Map<String, Object> constructParams(VirRequestPayload payload, String requestId) {
        return Map.of("regulatorReviewResponse", payload.getRegulatorReviewResponse());
    }
}
