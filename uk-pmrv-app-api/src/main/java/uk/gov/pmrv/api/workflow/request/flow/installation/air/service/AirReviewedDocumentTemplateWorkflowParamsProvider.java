package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;

import java.util.Map;

@Component
public class AirReviewedDocumentTemplateWorkflowParamsProvider
    implements DocumentTemplateWorkflowParamsProvider<AirRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.AIR_REVIEWED;
    }

    @Override
    public Map<String, Object> constructParams(final AirRequestPayload payload, String requestId) {
        return Map.of("regulatorReviewResponse", payload.getRegulatorReviewResponse());
    }
}
