package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

import java.util.Map;

@Component
@AllArgsConstructor
public class PermitNotificationRejectedDocumentTemplateWorkflowParamsProvider
    implements DocumentTemplateWorkflowParamsProvider<PermitNotificationRequestPayload> {

    private final PermitNotificationCommonDocumentTemplateWorkflowParamsProvider commonDocumentTemplateWorkflowParamsProvider;

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_REJECTED;
    }

    @Override
    public Map<String, Object> constructParams(final PermitNotificationRequestPayload payload, String requestId) {
        return commonDocumentTemplateWorkflowParamsProvider.constructParams(payload);
    }
}

