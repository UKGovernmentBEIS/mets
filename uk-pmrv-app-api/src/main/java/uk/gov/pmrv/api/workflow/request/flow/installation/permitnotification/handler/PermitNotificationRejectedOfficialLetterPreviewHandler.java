package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionDetails;

import java.util.List;
import java.util.Map;

@Service
public class PermitNotificationRejectedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    public PermitNotificationRejectedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                                  final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                                  final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }


    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final Request request = requestTask.getRequest();
        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload = (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> params = this.constructParams(taskPayload);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_NOTIFICATION_REFUSED,
                templateParams,
                "Permit Notification Refusal Letter.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_NOTIFICATION_REFUSED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_REVIEW);
    }

    private Map<String, Object> constructParams(final PermitNotificationApplicationReviewRequestTaskPayload payload) {

        return Map.of("officialNotice", ((PermitNotificationReviewDecisionDetails) payload.getReviewDecision().getDetails()).getOfficialNotice());
    }
}
