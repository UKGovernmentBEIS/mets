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

import java.util.List;

@Service
public class PermitSurrenderDeemedWithdrawnOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public PermitSurrenderDeemedWithdrawnOfficialLetterPreviewHandler(RequestTaskService requestTaskService, InstallationPreviewOfficialNoticeService previewOfficialNoticeService, DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final Request request = requestTask.getRequest();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE,
                templateParams,
                "permit_surrender_deemed_withdrawn_notice.pdf");
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW);
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE);
    }
}
