package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.util.Map;

@Component
public class DoalRejectedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<DoalRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.DOAL_REJECTED;
    }

    @Override
    public Map<String, Object> constructParams(DoalRequestPayload payload, String requestId) {
        return Map.of(
                "reportingYear", payload.getReportingYear(),
                "doal", payload.getDoal(),
                "authorityResponse", payload.getDoalAuthority().getAuthorityResponse()
        );
    }
}
