package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionDetails;

@Component
public class PermitNotificationRejectedDocumentTemplateWorkflowParamsProvider
    implements DocumentTemplateWorkflowParamsProvider<PermitNotificationRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_REJECTED;
    }

    @Override
    public Map<String, Object> constructParams(final PermitNotificationRequestPayload payload, String requestId) {

        return Map.of("officialNotice", ((PermitNotificationReviewDecisionDetails) payload.getReviewDecision().getDetails()).getOfficialNotice());
    }
}

