package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

@Service
public class EmpVariationUkEtsRejectedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    
    public EmpVariationUkEtsRejectedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                                 final AviationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                                 final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();

        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);
        final String operatorName = taskPayload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName();
        templateParams.getAccountParams().setName(operatorName);

        final Map<String, Object> variationParams = this.constructParams(taskPayload);
        templateParams.getParams().putAll(variationParams);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_UKETS_REJECTED,
            templateParams,
            "emp_variation_rejected.pdf"
        );
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.EMP_VARIATION_UKETS_REJECTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW);
    }
    
    private Map<String, Object> constructParams(final EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload) {

        final String reason = taskPayload.getDetermination().getReason();
        return Map.of("rejectionReason", reason);
    }
}
