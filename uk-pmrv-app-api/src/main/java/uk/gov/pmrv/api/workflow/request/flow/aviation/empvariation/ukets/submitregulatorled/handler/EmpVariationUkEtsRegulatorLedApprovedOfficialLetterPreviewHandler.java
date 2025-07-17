package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

@Service
public class EmpVariationUkEtsRegulatorLedApprovedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    
    public EmpVariationUkEtsRegulatorLedApprovedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
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
        final EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();

        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);
        final String operatorName = taskPayload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName();
        templateParams.getAccountParams().setName(operatorName);

        final Map<String, Object> variationParams = this.constructParams(taskPayload, request.getAccountId());
        templateParams.getParams().putAll(variationParams);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED,
            templateParams,
            "emp_ca_variation_approved.pdf"
        );
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT);
    }
    
    private Map<String, Object> constructParams(final EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload,
                                                final Long accountId) {
        
        final TreeMap<EmpUkEtsReviewGroup, EmpAcceptedVariationDecisionDetails> sortedDecisions = 
            new TreeMap<>(taskPayload.getReviewGroupDecisions());
        
        final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
            .values()
            .stream()
            .map(EmpAcceptedVariationDecisionDetails::getVariationScheduleItems)
            .flatMap(List::stream)
            .toList();

        final int consolidationNumber =
            emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId) + 1;

        final Map<String, Object> params = new HashMap<>();
        params.put("consolidationNumber", consolidationNumber);
        params.put("variationScheduleItems", reviewGroupsVariationScheduleItems);
        params.put("reason", taskPayload.getReasonRegulatorLed());

        return params;
    }
}
