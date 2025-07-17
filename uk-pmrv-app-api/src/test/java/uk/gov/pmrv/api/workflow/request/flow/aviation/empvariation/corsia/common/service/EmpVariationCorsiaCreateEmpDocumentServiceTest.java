package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaCreateEmpDocumentServiceTest {

	@InjectMocks
    private EmpVariationCorsiaCreateEmpDocumentService service;

    @Mock
    private RequestService requestService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmpCreateDocumentService createDocumentService;

    @Test
    void create() throws InterruptedException, ExecutionException{

        final String requestId = "1";
        final long accountId = 5L;
        final String signatory = "signatory";
        final EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
                .decisionNotification(DecisionNotification.builder()
                        .signatory(signatory)
                        .build())
                .build();
        final Request request =
                Request.builder().accountId(accountId).payload(requestPayload).build();
        final EmissionsMonitoringPlanCorsiaDTO empDto =
            EmissionsMonitoringPlanCorsiaDTO.builder().id("empId").build();
        final FileInfoDTO empDocument = FileInfoDTO.builder().uuid("uuid").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
                .thenReturn(Optional.of(empDto));
        when(createDocumentService.generateDocumentAsync(request, signatory, empDto, DocumentTemplateType.EMP_CORSIA))
                .thenReturn(CompletableFuture.completedFuture(empDocument));

        CompletableFuture<FileInfoDTO> result = service.create(requestId);
        
        assertThat(result.get()).isEqualTo(empDocument);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
        verify(createDocumentService, times(1)).generateDocumentAsync(request, signatory, empDto, DocumentTemplateType.EMP_CORSIA);
    }
}
