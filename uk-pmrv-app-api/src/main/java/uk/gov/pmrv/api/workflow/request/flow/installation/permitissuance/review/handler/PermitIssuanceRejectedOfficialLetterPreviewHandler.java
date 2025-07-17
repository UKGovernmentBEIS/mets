package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;

import java.util.List;
import java.util.Map;

@Service
public class PermitIssuanceRejectedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {
    
    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public PermitIssuanceRejectedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                              final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                              final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> params = this.constructParams(taskPayload);
        templateParams.setParams(params);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_REJECTED,
            templateParams,
            "permit_application_rejection_notice.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_ISSUANCE_REJECTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW
                ,RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW);
    }

    private Map<String, Object> constructParams(final PermitIssuanceApplicationReviewRequestTaskPayload payload) {

        final PermitIssuanceRejectDetermination determination =
            (PermitIssuanceRejectDetermination) payload.getDetermination();
        return Map.of("officialRefusalLetter", determination.getOfficialNotice());
    }
}
