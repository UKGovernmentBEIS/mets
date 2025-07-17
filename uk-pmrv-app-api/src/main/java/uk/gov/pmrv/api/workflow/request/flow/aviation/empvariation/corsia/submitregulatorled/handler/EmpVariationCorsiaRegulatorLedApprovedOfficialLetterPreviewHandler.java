package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;

@Service
public class EmpVariationCorsiaRegulatorLedApprovedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    private final DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;
    
    public EmpVariationCorsiaRegulatorLedApprovedOfficialLetterPreviewHandler(
        final RequestTaskService requestTaskService,
        final AviationPreviewOfficialNoticeService previewOfficialNoticeService,
        final DocumentFileGeneratorService documentFileGeneratorService,
        final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService,
        final DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver) {
        
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.emissionsMonitoringPlanQueryService = emissionsMonitoringPlanQueryService;
        this.documentTemplateLocationInfoResolver = documentTemplateLocationInfoResolver;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();

        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);
        
        final String operatorName = taskPayload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName();
        templateParams.getAccountParams().setName(operatorName);

        final LocationOnShoreStateDTO locationOnShoreStateDTO =
            taskPayload.getEmissionsMonitoringPlan().getOperatorDetails().getOrganisationStructure().getOrganisationLocation();
        final String locationString = documentTemplateLocationInfoResolver.constructLocationInfo(locationOnShoreStateDTO);
        templateParams.getAccountParams().setLocation(locationString);

        final Map<String, Object> variationParams = this.constructParams(taskPayload, request.getAccountId());
        templateParams.getParams().putAll(variationParams);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED,
            templateParams,
            "corsia_emp_ca_variation_approved.pdf"
        );
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT);
    }
    
    private Map<String, Object> constructParams(final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload,
                                                final Long accountId) {
        
        final TreeMap<EmpCorsiaReviewGroup, EmpAcceptedVariationDecisionDetails> sortedDecisions = 
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
