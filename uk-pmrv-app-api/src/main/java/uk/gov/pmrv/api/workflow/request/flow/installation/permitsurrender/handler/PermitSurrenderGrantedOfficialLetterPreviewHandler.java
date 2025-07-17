package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification.PermitSurrenderGrantedDocumentTemplateWorkflowParamsProvider;

import java.util.List;
import java.util.Map;

@Service
public class PermitSurrenderGrantedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final PermitSurrenderGrantedDocumentTemplateWorkflowParamsProvider workflowParamsProvider;

    public PermitSurrenderGrantedOfficialLetterPreviewHandler(RequestTaskService requestTaskService, InstallationPreviewOfficialNoticeService previewOfficialNoticeService, DocumentFileGeneratorService documentFileGeneratorService, PermitSurrenderGrantedDocumentTemplateWorkflowParamsProvider workflowParamsProvider) {
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

        final Map<String, Object> params = workflowParamsProvider.constructParams((PermitSurrenderRequestPayload) request.getPayload(), null);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_SURRENDERED_NOTICE,
                templateParams,
                "permit_surrender_notice.pdf");
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW);
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_SURRENDERED_NOTICE);
    }

}
