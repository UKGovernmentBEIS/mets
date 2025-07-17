package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditSubmittedDocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;

import java.util.List;
import java.util.Map;


@Service
public class InstallationAuditSubmitOfficialNoticePreviewHandler extends PreviewDocumentAbstractHandler  {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final InstallationAuditSubmittedDocumentTemplateWorkflowParamsProvider paramsProvider;


    public InstallationAuditSubmitOfficialNoticePreviewHandler(RequestTaskService
                                                                       requestTaskService,
                                                               final InstallationPreviewOfficialNoticeService
                                                                            previewOfficialNoticeService,
                                                               final DocumentFileGeneratorService
                                                                            documentFileGeneratorService,
                                                               final InstallationAuditSubmittedDocumentTemplateWorkflowParamsProvider
                                                                       paramsProvider) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.paramsProvider = paramsProvider;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final InstallationInspectionApplicationSubmitRequestTaskPayload taskPayload =
                (InstallationInspectionApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final InstallationAuditRequestPayload requestPayload =
                (InstallationAuditRequestPayload) request.getPayload();

        final TemplateParams templateParams =
                previewOfficialNoticeService.generateCommonParams(request, decisionNotification);


        final Map<String, Object> params = paramsProvider
                .constructParamsFromAuditSubmitRequestTaskPayload(taskPayload, requestPayload, request.getId());

        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.INSTALLATION_AUDIT_SUBMITTED,
                templateParams,
                "installation_audit_official_letter_preview.pdf");

    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.INSTALLATION_AUDIT_SUBMITTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(
            RequestTaskType.INSTALLATION_AUDIT_APPLICATION_SUBMIT,
            RequestTaskType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW
        );
    }

}
