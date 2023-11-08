package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBInitiateRequestService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferBInitiateRequestServiceTest {
    
    @InjectMocks
    private PermitTransferBInitiateRequestService service;
    
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private WorkflowService workflowService;
    
    @Test
    void initiatePermitTransferBRequest() {

        final String transferARequestId = "transferARequestId";
        final String transferABusinessKey = "transferABusinessKey";
        final String transferBRequestId = "transferBRequestId";
        final Long transferAAccountId = 1L;
        final Long transferBAccountId = 2L;
        final String transferCode = "123456789";
        final UUID reasonAttachment = UUID.randomUUID();
        final String reason = "the reason";
        final LocalDate transferDate = LocalDate.of(2022, 1, 1);
        final PermitTransferDetails permitTransferDetails = PermitTransferDetails.builder()
            .reason(reason)
            .reasonAttachments(Set.of(reasonAttachment))
            .transferDate(transferDate)
            .transferCode(transferCode)
            .aerLiable(TransferParty.RECEIVER)
            .payer(TransferParty.TRANSFERER)
            .build();
        final Request transferARequest = Request.builder()
            .accountId(transferAAccountId)
            .id(transferARequestId)
            .payload(PermitTransferARequestPayload.builder()
                .permitTransferDetails(permitTransferDetails)
                .transferAttachments(Map.of(reasonAttachment, "filename"))
                .build())
            .build();

        final RequestParams requestParams = RequestParams.builder()
            .type(RequestType.PERMIT_TRANSFER_B)
            .accountId(transferBAccountId)
            .requestPayload(PermitTransferBRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_TRANSFER_B_REQUEST_PAYLOAD)
                .relatedRequestId(transferARequestId)
                .permitTransferDetails(permitTransferDetails)
                .permitAttachments(Map.of(reasonAttachment, "filename"))
                .build())
            .requestMetadata(PermitTransferBRequestMetadata.builder().type(RequestMetadataType.PERMIT_TRANSFER_B).build())
            .processVars(Map.of(
                BpmnProcessConstants.SKIP_PAYMENT, true,
                BpmnProcessConstants.PERMIT_TRANSFER_TRANSFERRING_BUSINESS_KEY, transferABusinessKey)
            )
            .build();

        when(requestRepository.findById(transferARequestId)).thenReturn(Optional.of(transferARequest));
        when(installationAccountQueryService.getByActiveTransferCode(transferCode))
            .thenReturn(Optional.of(InstallationAccountDTO.builder().id(transferBAccountId).build()));
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id(transferBRequestId).build());

        service.initiatePermitTransferBRequest(transferARequestId, transferABusinessKey);

        verify(workflowService, times(1)).constructBusinessKey(transferBRequestId);
    }
}
