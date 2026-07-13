package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaPreviewEmpDocumentService implements EmpPreviewDocumentService {

    private final RequestTaskService requestTaskService;
    private final EmpCorsiaPreviewCreateEmpDocumentService empCorsiaPreviewCreateEmpDocumentService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final EmpVariationCorsiaApplicationRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final Long accountId = request.getAccountId();

        final EmissionsMonitoringPlanCorsia emp = taskPayload.getEmissionsMonitoringPlan();
        final ServiceContactDetails serviceContactDetails = taskPayload.getServiceContactDetails();
        final Map<UUID, String> attachments = taskPayload.getAttachments();

        final int consolidationNumber = 
            emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId) + 1;

        return empCorsiaPreviewCreateEmpDocumentService.getFile(
            decisionNotification,
            request,
            accountId,
            emp,
            serviceContactDetails,
            attachments,
            consolidationNumber
        );
    }

    @Override
    public List<RequestTaskType> getTypes() {
        return List.of(
                RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW,
                RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT,
                RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW,
                RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW,
                RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW,
                RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT,
                RequestTaskType.EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW,
                RequestTaskType.EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS,
                RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT,
                RequestTaskType.EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW
        );
    }


}
