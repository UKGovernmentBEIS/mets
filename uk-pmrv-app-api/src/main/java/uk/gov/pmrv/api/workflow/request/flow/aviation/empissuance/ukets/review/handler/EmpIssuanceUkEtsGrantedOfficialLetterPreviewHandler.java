package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.handler.EmpIssuanceOfficialLetterPreviewHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
public class EmpIssuanceUkEtsGrantedOfficialLetterPreviewHandler extends EmpIssuanceOfficialLetterPreviewHandler {

    private final DocumentFileGeneratorService documentFileGeneratorService;

    public EmpIssuanceUkEtsGrantedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                               final AviationPreviewOfficialNoticeService
                                                                       previewOfficialNoticeService,
                                                               final DocumentFileGeneratorService
                                                                       documentFileGeneratorService) {
        super(requestTaskService, previewOfficialNoticeService);
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {

        final TemplateParams templateParams = this.constructTemplateParams(taskId,decisionNotification);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_ISSUANCE_UKETS_GRANTED,
            templateParams,
            "emp_application_approved.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.EMP_ISSUANCE_UKETS_GRANTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW,
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW);
    }

    @Override
    protected String getOperatorNameFromRequestTaskPayload(RequestTaskPayload requestTaskPayload) {
        return ((EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) (requestTaskPayload))
                .getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName();
    }
}
