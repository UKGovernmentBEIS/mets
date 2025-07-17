package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider;

import java.util.List;
import java.util.Map;


@Service
public class InstallationOnsiteInspectionSubmitOfficialNoticePreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final InstallationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider paramsProvider;


    public InstallationOnsiteInspectionSubmitOfficialNoticePreviewHandler(RequestTaskService requestTaskService,
                                                               final InstallationPreviewOfficialNoticeService
                                                                            previewOfficialNoticeService,
                                                               final DocumentFileGeneratorService
                                                                            documentFileGeneratorService,
                                                               final InstallationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider
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
        final InstallationOnsiteInspectionRequestPayload requestPayload =
                (InstallationOnsiteInspectionRequestPayload) request.getPayload();

        final TemplateParams templateParams =
                previewOfficialNoticeService.generateCommonParams(request, decisionNotification);


        final Map<String, Object> params = paramsProvider
                .constructParamsFromOnsiteInspectionSubmitRequestTaskPayload(taskPayload, requestPayload, request.getId());

        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED,
                templateParams,
                "installation_onsite_inspection_official_letter_preview.pdf");

    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(
            RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT,
            RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW
        );
    }


}
