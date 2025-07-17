package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.EmpIssuanceCorsiaPeerReviewPreviewEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaPreviewEmpDocumentServiceTest {

    @InjectMocks
    private EmpIssuanceCorsiaPeerReviewPreviewEmpDocumentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmpCorsiaPreviewCreateEmpDocumentService empCorsiaPreviewCreateEmpDocumentService;

    @Test
    void create() {

        final long accountId = 2L;
        final long taskId = 2L;
        final Request request = Request.builder().accountId(accountId).build();
        final EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder().build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "filename");
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .request(request)
            .payload(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emp)
                .serviceContactDetails(serviceContactDetails)
                .empAttachments(attachments)
                .build())
            .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        
        service.create(taskId, decisionNotification);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(empCorsiaPreviewCreateEmpDocumentService, times(1)).getFile(
            decisionNotification, request, accountId, emp, serviceContactDetails, attachments, 1
        );
    }
}
