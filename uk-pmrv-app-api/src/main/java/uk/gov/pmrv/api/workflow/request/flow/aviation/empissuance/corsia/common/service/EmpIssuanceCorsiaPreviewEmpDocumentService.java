package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@RequiredArgsConstructor
public abstract class EmpIssuanceCorsiaPreviewEmpDocumentService implements EmpPreviewDocumentService {

    private final RequestTaskService requestTaskService;
    private final EmpCorsiaPreviewCreateEmpDocumentService empCorsiaPreviewCreateEmpDocumentService;
    

    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final Long accountId = request.getAccountId();
        
        final EmissionsMonitoringPlanCorsia emp = taskPayload.getEmissionsMonitoringPlan();
        final ServiceContactDetails serviceContactDetails = taskPayload.getServiceContactDetails();
        final Map<UUID, String> attachments = taskPayload.getAttachments();

        final int consolidationNumber = 1; // consolidation number default value

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

}
