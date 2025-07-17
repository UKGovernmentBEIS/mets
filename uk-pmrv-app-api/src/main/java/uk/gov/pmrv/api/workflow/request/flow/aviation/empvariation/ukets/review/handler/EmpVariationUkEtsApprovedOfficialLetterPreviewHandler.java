package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

@Service
public class EmpVariationUkEtsApprovedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    
    public EmpVariationUkEtsApprovedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                                 final AviationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                                 final DocumentFileGeneratorService documentFileGeneratorService,
                                                                 final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.emissionsMonitoringPlanQueryService = emissionsMonitoringPlanQueryService;
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

        final Map<String, Object> variationParams = this.constructParams(taskPayload, request.getAccountId());
        templateParams.getParams().putAll(variationParams);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_UKETS_ACCEPTED,
            templateParams,
            "emp_variation_approved.pdf"
        );
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.EMP_VARIATION_UKETS_ACCEPTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW);
    }
    
    private Map<String, Object> constructParams(final EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload,
                                                final Long accountId) {
    	final EmpAcceptedVariationDecisionDetails variationDecisionDetails = (EmpAcceptedVariationDecisionDetails) taskPayload
				.getEmpVariationDetailsReviewDecision().getDetails();
    	
        final TreeMap<EmpUkEtsReviewGroup, EmpVariationReviewDecision> sortedDecisions = 
            new TreeMap<>(taskPayload.getReviewGroupDecisions());
        
        final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
            .values()
            .stream()
            .filter(empVariationReviewDecision -> empVariationReviewDecision.getType() == EmpVariationReviewDecisionType.ACCEPTED)
            .map(EmpVariationReviewDecision::getDetails)
            .map(EmpAcceptedVariationDecisionDetails.class::cast)
            .map(EmpAcceptedVariationDecisionDetails::getVariationScheduleItems)
            .flatMap(List::stream)
            .toList();

        final int consolidationNumber =
            emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId) + 1;

        final Map<String, Object> params = new HashMap<>();
        params.put("empConsolidationNumber", consolidationNumber);
        params.put("variationScheduleItems",
				Stream.concat(variationDecisionDetails.getVariationScheduleItems().stream(),
						reviewGroupsVariationScheduleItems.stream()).toList());
        
        return params;
    }
}
