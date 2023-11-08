package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsCreateEmpDocumentServiceTest {

    @InjectMocks
    private EmpIssuanceUkEtsCreateEmpDocumentService cut;

    @Mock
    private RequestService requestService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmpCreateDocumentService empCreateDocumentService;

    @Test
    void create() {

        final String requestId = "1";
        final long accountId = 5L;
        final String signatory = "signatory";
        final EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
                .decisionNotification(DecisionNotification.builder()
                        .signatory(signatory)
                        .build())
                .build();
        final Request request =
                Request.builder().accountId(accountId).payload(requestPayload).build();
        final EmissionsMonitoringPlanUkEtsDTO empDto =
                EmissionsMonitoringPlanUkEtsDTO.builder().id("empId").build();
        final FileInfoDTO empDocument = FileInfoDTO.builder().uuid("uuid").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId))
                .thenReturn(Optional.of(empDto));
        when(empCreateDocumentService.generateDocumentAsync(request, signatory, empDto, DocumentTemplateType.EMP_UKETS))
                .thenReturn(CompletableFuture.completedFuture(empDocument));

        cut.create(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        verify(empCreateDocumentService, times(1)).generateDocumentAsync(request, signatory, empDto, DocumentTemplateType.EMP_UKETS);
    }
}
