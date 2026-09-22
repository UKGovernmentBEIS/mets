package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.notification.PermitRevocationCessationDocumentTemplateWorkflowParamsProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PermitRevocationCessationNotifyOperatorOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final PermitRevocationCessationDocumentTemplateWorkflowParamsProvider workflowParamsProvider;


    public PermitRevocationCessationNotifyOperatorOfficialLetterPreviewHandler(RequestTaskService requestTaskService, InstallationPreviewOfficialNoticeService previewOfficialNoticeService,PermitRevocationCessationDocumentTemplateWorkflowParamsProvider workflowParamsProvider, DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.workflowParamsProvider = workflowParamsProvider;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final Request request = requestTask.getRequest();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParamsWithExtraAccountDetails(request, decisionNotification);
        PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload = (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();
        requestPayload.setPermitCessationCompletedDate(LocalDate.now());
        requestPayload.setPermitCessationContainer(requestTaskPayload.getCessationContainer());
        final Map<String, Object> params = workflowParamsProvider.constructParams(requestPayload, null);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_REVOCATION_CESSATION,
                templateParams,
                "permit_revocation_cessation_notice.pdf");
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_REVOCATION_CESSATION_SUBMIT);
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_REVOCATION_CESSATION);
    }
}
