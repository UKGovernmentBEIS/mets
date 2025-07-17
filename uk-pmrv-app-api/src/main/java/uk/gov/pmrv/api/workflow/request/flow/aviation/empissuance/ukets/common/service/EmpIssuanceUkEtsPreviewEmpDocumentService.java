package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets.EmpUkEtsPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@RequiredArgsConstructor
public abstract class EmpIssuanceUkEtsPreviewEmpDocumentService implements EmpPreviewDocumentService {

    private final RequestTaskService requestTaskService;
    private final EmpUkEtsPreviewCreateEmpDocumentService empUkEtsPreviewCreateEmpDocumentService;


    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();
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

}
