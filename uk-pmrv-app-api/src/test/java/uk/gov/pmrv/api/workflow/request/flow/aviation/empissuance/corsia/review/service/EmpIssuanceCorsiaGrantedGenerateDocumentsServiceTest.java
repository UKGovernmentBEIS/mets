package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

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
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaGrantedGenerateDocumentsServiceTest {

    @InjectMocks
    private EmpIssuanceCorsiaGrantedGenerateDocumentsService empIssuanceGrantedGenerateDocumentsService;

    @Mock
    private RequestService requestService;

    @Mock
    private EmpIssuanceCorsiaCreateEmpDocumentService empIssuanceCreateEmpDocumentService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmissionsMonitoringPlanService emissionsMonitoringPlanService;

    @Mock
    private EmpIssuanceCorsiaOfficialNoticeService empIssuanceOfficialNoticeService;

    @Test
    void generateDocuments() {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";

        final EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
                .decisionNotification(DecisionNotification.builder()
                        .signatory(signatory)
                        .build())
                .build();

        final Request request = Request.builder()
                .type(RequestType.EMP_ISSUANCE_CORSIA)
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        UUID empPdfUuid = UUID.randomUUID();
        FileInfoDTO empDocument = FileInfoDTO.builder()
                .name("permit.pdf")
                .uuid(empPdfUuid.toString())
                .build();

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        final EmissionsMonitoringPlanCorsiaDTO empDto =
            EmissionsMonitoringPlanCorsiaDTO.builder()
                        .id("empId")
                        .consolidationNumber(1)
                        .accountId(accountId)
                        .build();

        when(empIssuanceCreateEmpDocumentService.create(requestId))
                .thenReturn(CompletableFuture.completedFuture(empDocument));
        when(empIssuanceOfficialNoticeService.generateGrantedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
                .thenReturn(Optional.of(empDto));

        empIssuanceGrantedGenerateDocumentsService.generateDocuments(requestId);

        verify(empIssuanceCreateEmpDocumentService, times(1)).create(requestId);
        verify(empIssuanceOfficialNoticeService, times(1)).generateGrantedOfficialNotice(requestId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
        verify(emissionsMonitoringPlanService, times(1)).setFileDocumentUuid(empDto.getId(), empPdfUuid.toString());

        assertThat(requestPayload.getEmpDocument()).isEqualTo(empDocument);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }

    @Test
    void generateDocuments_throws_business_exception() {
        final String requestId = "1";
        final String signatory = "signatory";

        final EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
                .decisionNotification(DecisionNotification.builder()
                        .signatory(signatory)
                        .build())
                .build();

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(empIssuanceCreateEmpDocumentService.create(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "permit.pdf"));
            return future;
        });

        when(empIssuanceOfficialNoticeService.generateGrantedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        BusinessException be = assertThrows(BusinessException.class, () -> empIssuanceGrantedGenerateDocumentsService
                .generateDocuments(requestId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);

        verify(empIssuanceCreateEmpDocumentService, times(1)).create(requestId);
        verify(empIssuanceOfficialNoticeService, times(1)).generateGrantedOfficialNotice(requestId);
        verifyNoInteractions(requestService, emissionsMonitoringPlanQueryService, emissionsMonitoringPlanService);

        assertThat(requestPayload.getEmpDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        final String requestId = "1";
        final String signatory = "signatory";

        final EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
                .decisionNotification(DecisionNotification.builder()
                        .signatory(signatory)
                        .build())
                .build();

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(empIssuanceCreateEmpDocumentService.create(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("something unexpected happened"));
            return future;
        });

        when(empIssuanceOfficialNoticeService.generateGrantedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        BusinessException be = assertThrows(BusinessException.class, () -> empIssuanceGrantedGenerateDocumentsService.
                generateDocuments(requestId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);

        verify(empIssuanceCreateEmpDocumentService, times(1)).create(requestId);
        verify(empIssuanceOfficialNoticeService, times(1)).generateGrantedOfficialNotice(requestId);
        verifyNoInteractions(requestService, emissionsMonitoringPlanQueryService, emissionsMonitoringPlanService);

        assertThat(requestPayload.getEmpDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
    }
}
