package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermitTransferBInitiateRequestService {
    
    private final RequestRepository requestRepository;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final StartProcessRequestService startProcessRequestService;
    private final WorkflowService workflowService;
    
    public String initiatePermitTransferBRequest(final String transferARequestId,
                                                 final String transferABusinessKey) {

        final Request transferARequest = requestRepository.findById(transferARequestId).orElseThrow(
            () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );
        final PermitTransferARequestPayload transferARequestPayload = (PermitTransferARequestPayload) transferARequest.getPayload();
        final String transferCode = transferARequestPayload.getPermitTransferDetails().getTransferCode();
        final Optional<InstallationAccountDTO> receivingAccount = installationAccountQueryService.getByActiveTransferCode(transferCode);
        if (receivingAccount.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        final Long transferBAccountId = receivingAccount.get().getId();
        final boolean skipPayment = transferARequestPayload.getPermitTransferDetails().getPayer() == TransferParty.TRANSFERER;

        final RequestParams params = RequestParams.builder()
            .type(RequestType.PERMIT_TRANSFER_B)
            .accountId(transferBAccountId)
            .requestPayload(PermitTransferBRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_TRANSFER_B_REQUEST_PAYLOAD)
                .permitTransferDetails(transferARequestPayload.getPermitTransferDetails())
                .permitAttachments(transferARequestPayload.getTransferAttachments())
                .relatedRequestId(transferARequestId)
                .build())
            .requestMetadata(PermitTransferBRequestMetadata.builder()
                .type(RequestMetadataType.PERMIT_TRANSFER_B)
                .build())
            .processVars(Map.of(
                BpmnProcessConstants.SKIP_PAYMENT, skipPayment,
                BpmnProcessConstants.PERMIT_TRANSFER_TRANSFERRING_BUSINESS_KEY, transferABusinessKey)
            )
            .build();

        final Request transferBRequest = startProcessRequestService.startProcess(params);

        final String transferBRequestId = transferBRequest.getId();
        transferARequestPayload.setRelatedRequestId(transferBRequestId);
        
        return workflowService.constructBusinessKey(transferBRequestId);
    }
}
