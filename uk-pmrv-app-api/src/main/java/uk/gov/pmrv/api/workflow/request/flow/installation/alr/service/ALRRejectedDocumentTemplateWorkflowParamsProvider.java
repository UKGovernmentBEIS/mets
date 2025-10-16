package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.util.Map;


@Component
public class ALRRejectedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<ALRRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.ALR_REJECTED;
    }

    @Override
    public Map<String, Object> constructParams(ALRRequestPayload payload, String requestId) {
        return Map.of(
                "reportingYear", payload.getReportingYear(),
                "alr", payload.getRegulatorReviewOutcome(),
                "authorityResponse", payload.getAuthorityReviewOutcome().getAuthorityResponse()
        );
    }
}
