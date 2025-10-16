package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCreationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PermitTransferAAlrServiceTest {

    @InjectMocks
    private PermitTransferAAlrService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private DateService dateService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private ALRCreationService alrCreationService;

    @Mock
    private InstallationAccountUpdateService installationAccountUpdateService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void process_whenReceiverAlrLiableAndAlrExists_thenCreateReceiverAlr() {

        final long transfererAccountId = 1L;
        final long receiverAccountId = 2L;
        final String requestId = "requestId";
        final String relatedRequestId = "relatedRequestId";
        final String alrRequestId = "alrRequestId";
        final String alrProcessInstanceId = "alrProcessInstanceId";
        final Request transferARequest = Request.builder()
                .id(requestId)
                .accountId(transfererAccountId)
                .payload(PermitTransferARequestPayload.builder()
                        .relatedRequestId(relatedRequestId)
                        .permitTransferDetails(PermitTransferDetails.builder()
                                .alrLiable(TransferParty.RECEIVER)
                                .build()).build())
                .build();
        final Request transferBRequest = Request.builder().accountId(receiverAccountId).build();
        final LocalDateTime now = LocalDateTime.of(2022, 1, 2, 3, 4, 5);
        final Request alrRequest = Request.builder()
                .id(alrRequestId)
                .type(RequestType.ALR)
                .processInstanceId(alrProcessInstanceId)
                .metadata(ALRRequestMetaData.builder().year(Year.of(2022)).build())
                .accountId(1L)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);
        when(dateService.getLocalDateTime()).thenReturn(now);
        when(requestRepository.findByAccountIdAndTypeInAndStatus(transfererAccountId, List.of(RequestType.ALR), RequestStatus.COMPLETED))
                .thenReturn(List.of());
        when(requestRepository.findByAccountIdAndTypeInAndStatus(transfererAccountId, List.of(RequestType.ALR), RequestStatus.IN_PROGRESS))
                .thenReturn(List.of(alrRequest));
        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);
        when(requestService.findRequestById(relatedRequestId)).thenReturn(transferBRequest);
        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(InstallationAccountDTO.builder().faStatus(true).build());

        service.process(requestId);

        verify(workflowService, times(1)).deleteProcessInstance(alrProcessInstanceId, "Alr workflow terminated because of permit transfer");
        assertEquals(RequestStatus.CANCELLED, alrRequest.getStatus());
        verify(requestService, times(1)).addActionToRequest(
                alrRequest,
                null,
                RequestActionType.REQUEST_TERMINATED,
                null
        );
        verify(alrCreationService, times(1)).createALR(receiverAccountId, false, Optional.empty());
    }

    @Test
    void process_whenTransfererAlrLiable_thenDoNothing() {

        final long transfererAccountId = 1L;
        final String requestId = "requestId";
        final String relatedRequestId = "relatedRequestId";
        final Request transferARequest = Request.builder()
                .id(requestId)
                .accountId(transfererAccountId)
                .payload(PermitTransferARequestPayload.builder()
                        .relatedRequestId(relatedRequestId)
                        .permitTransferDetails(PermitTransferDetails.builder()
                                .alrLiable(TransferParty.TRANSFERER)
                                .build()).build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);
        when(requestService.findRequestById(relatedRequestId)).thenReturn(Request.builder().accountId(transfererAccountId).build());
        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(InstallationAccountDTO.builder().faStatus(true).build());

        service.process(requestId);
    }

    @Test
    void process_whenAlrNotExists_thenDoNothing() {

        final long transfererAccountId = 1L;
        final String requestId = "requestId";
        final String relatedRequestId = "relatedRequestId";
        final String aerRequestId = "aerRequestId";
        final String aerProcessInstanceId = "aerProcessInstanceId";
        final Request transferARequest = Request.builder()
                .id(requestId)
                .accountId(transfererAccountId)
                .payload(PermitTransferARequestPayload.builder()
                        .relatedRequestId(relatedRequestId)
                        .permitTransferDetails(PermitTransferDetails.builder()
                                .alrLiable(TransferParty.RECEIVER)
                                .build()).build())
                .build();
        final LocalDateTime now = LocalDateTime.of(2022, 1, 2, 3, 4, 5);
        final Request alrRequest = Request.builder()
                .id(aerRequestId)
                .processInstanceId(aerProcessInstanceId)
                .metadata(ALRRequestMetaData.builder().year(Year.of(2020)).build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);
        when(dateService.getLocalDateTime()).thenReturn(now);
        when(requestRepository.findByAccountIdAndTypeInAndStatus(transfererAccountId, List.of(RequestType.ALR), RequestStatus.COMPLETED))
                .thenReturn(List.of());
        when(requestRepository.findByAccountIdAndTypeInAndStatus(transfererAccountId, List.of(RequestType.ALR), RequestStatus.IN_PROGRESS))
                .thenReturn(List.of(alrRequest));
        when(requestService.findRequestById(relatedRequestId)).thenReturn(Request.builder().accountId(transfererAccountId).build());
        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(InstallationAccountDTO.builder().faStatus(true).build());


        service.process(requestId);
    }

    @Test
    void verifyThatTransfererAndReceiverHaveEqualFaStatus() {
        String requestId = "REQ-1";

        // "transferer" request
        Request transfererRequest = new Request();
        transfererRequest.setAccountId(10L);
        PermitTransferARequestPayload payload = new PermitTransferARequestPayload();
        payload.setPermitTransferDetails(new PermitTransferDetails());
        payload.setRelatedRequestId("REQ-2");
        transfererRequest.setPayload(payload);

        // "receiver" related request
        Request receiverRequest = new Request();
        receiverRequest.setAccountId(20L);

        // DTO for the transferer's account
        InstallationAccountDTO transfererAccountDTO = new InstallationAccountDTO();
        transfererAccountDTO.setFaStatus(Boolean.TRUE);

        // stubbing
        when(requestService.findRequestById("REQ-1")).thenReturn(transfererRequest);
        when(requestService.findRequestById("REQ-2")).thenReturn(receiverRequest);
        when(installationAccountQueryService.getAccountDTOById(10L)).thenReturn(transfererAccountDTO);

        // when
        service.process(requestId);

        // then
        verify(installationAccountUpdateService).updateFaStatus(
                eq(20L),
                argThat(dto -> Boolean.TRUE.equals(dto.getFaStatus()))
        );
    }

}
