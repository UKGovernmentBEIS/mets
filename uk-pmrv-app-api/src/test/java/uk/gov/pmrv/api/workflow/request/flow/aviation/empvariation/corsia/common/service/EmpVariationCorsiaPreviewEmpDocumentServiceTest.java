package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewPreviewEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaPreviewEmpDocumentServiceTest {

    @InjectMocks
    private EmpVariationCorsiaReviewPreviewEmpDocumentService service;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    
    @Mock
    private EmpCorsiaPreviewCreateEmpDocumentService empCorsiaPreviewCreateEmpDocumentService;
    
    @Test
    void createEmpForVariation() {

        final Long taskId = 100L;
        final Long accountId = 200L;
        final String signatory = "signatory";
        final DecisionNotification decisionNotification = DecisionNotification.builder().signatory(signatory).build();
        final Request request = Request.builder()
            .accountId(accountId)
            .build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "filename");
        final EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder().build();
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emp)
                .serviceContactDetails(serviceContactDetails)
                .empAttachments(attachments)
                .build())
            .build();
        final int consolidationNumber = 2;
        final int newConsolidationNumber = consolidationNumber + 1;
        final String fileName = "fileName";
        final FileDTO fileDTO = FileDTO.builder().fileName(fileName).build();
        
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId)).thenReturn(consolidationNumber);
        when(empCorsiaPreviewCreateEmpDocumentService.getFile(decisionNotification, request, accountId, emp,
            serviceContactDetails, attachments, newConsolidationNumber)).thenReturn(fileDTO);

        final FileDTO result = service.create(taskId, decisionNotification);
        
        assertEquals(result.getFileName(), fileName);
    }
}
