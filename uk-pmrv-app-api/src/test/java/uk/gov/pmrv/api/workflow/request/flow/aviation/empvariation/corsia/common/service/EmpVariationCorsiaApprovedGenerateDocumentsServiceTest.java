package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaApprovedGenerateDocumentsServiceTest {

	
	@InjectMocks
    private EmpVariationCorsiaApprovedGenerateDocumentsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private EmpVariationCorsiaCreateEmpDocumentService createEmpDocumentService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmissionsMonitoringPlanService emissionsMonitoringPlanService;

    @Mock
    private EmpVariationCorsiaOfficialNoticeService officialNoticeService;

    @Test
    void generateDocuments() {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";

        final EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();

        final Request request = Request.builder()
            .type(RequestType.EMP_VARIATION_CORSIA)
            .accountId(accountId)
            .payload(requestPayload)
            .build();

        UUID empUuid = UUID.randomUUID();
        FileInfoDTO empDocument = FileInfoDTO.builder()
            .name("emp.pdf")
            .uuid(empUuid.toString())
            .build();

        UUID officialNoticeUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
            .name("official notice.pdf")
            .uuid(officialNoticeUuid.toString())
            .build();

        final EmissionsMonitoringPlanCorsiaDTO empDto =
            EmissionsMonitoringPlanCorsiaDTO.builder()
                .id("empId")
                .consolidationNumber(5)
                .accountId(accountId)
                .build();

        when(createEmpDocumentService.create(requestId))
            .thenReturn(CompletableFuture.completedFuture(empDocument));
        when(officialNoticeService.generateApprovedOfficialNotice(requestId))
            .thenReturn(CompletableFuture.completedFuture(officialNotice));
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
            .thenReturn(Optional.of(empDto));

        service.generateDocuments(requestId, false);

        verify(createEmpDocumentService, times(1)).create(requestId);
        verify(officialNoticeService, times(1)).generateApprovedOfficialNotice(requestId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
        verify(emissionsMonitoringPlanService, times(1)).setFileDocumentUuid(empDto.getId(), empUuid.toString());

        assertThat(requestPayload.getEmpDocument()).isEqualTo(empDocument);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }
    
    @Test
    void generateDocuments_regulator_led() {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";

        final EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
                .decisionNotification(DecisionNotification.builder()
                        .signatory(signatory)
                        .build())
                .build();

        final Request request = Request.builder()
                .type(RequestType.EMP_VARIATION_CORSIA)
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        UUID empUuid = UUID.randomUUID();
        FileInfoDTO empDocument = FileInfoDTO.builder()
                .name("emp.pdf")
                .uuid(empUuid.toString())
                .build();

        UUID officialNoticeUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("official notice.pdf")
                .uuid(officialNoticeUuid.toString())
                .build();

        final EmissionsMonitoringPlanCorsiaDTO empDto =
            EmissionsMonitoringPlanCorsiaDTO.builder()
                        .id("empId")
                        .consolidationNumber(5)
                        .accountId(accountId)
                        .build();

        when(createEmpDocumentService.create(requestId))
                .thenReturn(CompletableFuture.completedFuture(empDocument));
        when(officialNoticeService.generateApprovedOfficialNoticeRegulatorLed(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
                .thenReturn(Optional.of(empDto));

        service.generateDocuments(requestId, true);

        verify(createEmpDocumentService, times(1)).create(requestId);
        verify(officialNoticeService, times(1)).generateApprovedOfficialNoticeRegulatorLed(requestId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
        verify(emissionsMonitoringPlanService, times(1)).setFileDocumentUuid(empDto.getId(), empUuid.toString());

        assertThat(requestPayload.getEmpDocument()).isEqualTo(empDocument);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }

    @Test
    void generateDocuments_throws_business_exception() {
        final String requestId = "1";
        final String signatory = "signatory";

        final EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();

        UUID officialNoticeUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
            .name("official notice.pdf")
            .uuid(officialNoticeUuid.toString())
            .build();

        when(createEmpDocumentService.create(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "emp.pdf"));
            return future;
        });

        when(officialNoticeService.generateApprovedOfficialNotice(requestId))
            .thenReturn(CompletableFuture.completedFuture(officialNotice));

        BusinessException be = assertThrows(BusinessException.class, () ->
            service.generateDocuments(requestId, false));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);

        verify(createEmpDocumentService, times(1)).create(requestId);
        verify(officialNoticeService, times(1)).generateApprovedOfficialNotice(requestId);
        verifyNoInteractions(requestService, emissionsMonitoringPlanQueryService, emissionsMonitoringPlanService);

        assertThat(requestPayload.getEmpDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        final String requestId = "1";
        final String signatory = "signatory";

        final EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();

        UUID officialNoticeUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
            .name("official notice.pdf")
            .uuid(officialNoticeUuid.toString())
            .build();

        when(createEmpDocumentService.create(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("interrupted exception"));
            return future;
        });

        when(officialNoticeService.generateApprovedOfficialNotice(requestId))
            .thenReturn(CompletableFuture.completedFuture(officialNotice));

        BusinessException be = assertThrows(BusinessException.class, () -> service.
            generateDocuments(requestId, false));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);

        verify(createEmpDocumentService, times(1)).create(requestId);
        verify(officialNoticeService, times(1)).generateApprovedOfficialNotice(requestId);
        verifyNoInteractions(requestService, emissionsMonitoringPlanQueryService, emissionsMonitoringPlanService);

        assertThat(requestPayload.getEmpDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
    }
}
