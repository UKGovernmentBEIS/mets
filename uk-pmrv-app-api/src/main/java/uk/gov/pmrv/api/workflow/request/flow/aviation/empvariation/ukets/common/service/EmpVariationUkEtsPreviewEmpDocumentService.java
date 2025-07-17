package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets.EmpUkEtsPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@RequiredArgsConstructor
public abstract class EmpVariationUkEtsPreviewEmpDocumentService implements EmpPreviewDocumentService {

    private final RequestTaskService requestTaskService;
    private final EmpUkEtsPreviewCreateEmpDocumentService empUkEtsPreviewCreateEmpDocumentService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final EmpVariationUkEtsApplicationRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final Long accountId = request.getAccountId();

        final EmissionsMonitoringPlanUkEts emp = taskPayload.getEmissionsMonitoringPlan();
        final ServiceContactDetails serviceContactDetails = taskPayload.getServiceContactDetails();
        final Map<UUID, String> attachments = taskPayload.getAttachments();

        final int consolidationNumber = 
            emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId) + 1;

        return empUkEtsPreviewCreateEmpDocumentService.getFile(
            decisionNotification,
            request,
            accountId,
            emp,
            serviceContactDetails,
            attachments,
            consolidationNumber
        );
    }

}
