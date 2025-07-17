package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.util.List;

@Service
public class PermitIssuanceGrantedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {
    
    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public PermitIssuanceGrantedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                             final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                             final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final Request request = requestTask.getRequest();
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        DocumentTemplateType documentTemplateType;

        if(requestPayload.getPermitType().equals(PermitType.GHGE)) {
            documentTemplateType = DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED;
        } else if (requestPayload.getPermitType().equals(PermitType.HSE)) {
            documentTemplateType = DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED;
        } else {
            documentTemplateType = DocumentTemplateType.PERMIT_ISSUANCE_WASTE_ACCEPTED;
        }

        final String fileName = requestPayload.getPermitType() + "_permit_application_approved.pdf";

        return documentFileGeneratorService.generateFileDocument(
            documentTemplateType,
            templateParams,
            fileName);
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED,
                DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED,
                DocumentTemplateType.PERMIT_ISSUANCE_WASTE_ACCEPTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW,
                RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW);
    }
}
