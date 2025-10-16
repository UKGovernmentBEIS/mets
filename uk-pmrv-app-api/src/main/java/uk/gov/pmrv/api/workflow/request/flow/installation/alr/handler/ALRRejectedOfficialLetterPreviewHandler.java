package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;

import java.util.List;
import java.util.Map;


@Service
public class ALRRejectedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public ALRRejectedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                   final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                   final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final ALRAuthorityResponseSubmitRequestTaskPayload taskPayload =
                (ALRAuthorityResponseSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> params = this.constructParams(taskPayload, requestPayload);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.ALR_REJECTED,
                templateParams,
                "Activity_level_report_approved_by_Authority_notice.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.ALR_REJECTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.ALR_AUTHORITY_RESPONSE_SUBMIT);
    }

    private Map<String, Object> constructParams(ALRAuthorityResponseSubmitRequestTaskPayload taskPayload, ALRRequestPayload requestPayload) {
     return Map.of(
                "reportingYear", requestPayload.getReportingYear(),
                "alr", requestPayload.getRegulatorReviewOutcome(),
                "authorityResponse", taskPayload.getAuthorityReviewOutcome().getAuthorityResponse()
        );
    }
}
