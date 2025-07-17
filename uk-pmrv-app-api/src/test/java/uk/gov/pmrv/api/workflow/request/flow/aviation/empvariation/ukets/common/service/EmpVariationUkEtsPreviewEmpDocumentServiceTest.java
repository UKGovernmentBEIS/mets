package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets.EmpUkEtsPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewPreviewEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsPreviewEmpDocumentServiceTest {

    @InjectMocks
    private EmpVariationUkEtsReviewPreviewEmpDocumentService service;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    
    @Mock
    private EmpUkEtsPreviewCreateEmpDocumentService empUkEtsPreviewCreateEmpDocumentService;
    
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
        final EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder().build();
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
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
        when(empUkEtsPreviewCreateEmpDocumentService.getFile(decisionNotification, request, accountId, emp,
            serviceContactDetails, attachments, newConsolidationNumber)).thenReturn(fileDTO);

        final FileDTO result = service.create(taskId, decisionNotification);
        
        assertEquals(result.getFileName(), fileName);
    }
}
