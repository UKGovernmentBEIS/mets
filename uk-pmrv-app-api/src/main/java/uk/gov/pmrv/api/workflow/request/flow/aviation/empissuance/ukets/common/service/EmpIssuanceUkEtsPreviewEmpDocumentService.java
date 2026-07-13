package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets.EmpUkEtsPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpIssuanceUkEtsApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmpIssuanceUkEtsPreviewEmpDocumentService implements EmpPreviewDocumentService {

    private final RequestTaskService requestTaskService;
    private final EmpUkEtsPreviewCreateEmpDocumentService empUkEtsPreviewCreateEmpDocumentService;


    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final EmpIssuanceUkEtsApplicationRequestTaskPayload taskPayload =
            (EmpIssuanceUkEtsApplicationRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final Long accountId = request.getAccountId();
        
        final EmissionsMonitoringPlanUkEts emp = taskPayload.getEmissionsMonitoringPlan();
        final ServiceContactDetails serviceContactDetails = taskPayload.getServiceContactDetails();
        final Map<UUID, String> attachments = taskPayload.getAttachments();

        final int consolidationNumber = 1; // consolidation number default value

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

    @Override
    public List<RequestTaskType> getTypes() {
        return List.of(
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW,
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW,
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT,
                RequestTaskType.EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW,
                RequestTaskType.EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS,
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT,
                RequestTaskType.EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW);
    }

}
