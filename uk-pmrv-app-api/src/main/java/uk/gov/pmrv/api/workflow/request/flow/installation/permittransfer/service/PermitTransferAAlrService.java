package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateFaStatusDTO;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermitTransferAAlrService {

    private static final String DELETE_REASON = "Alr workflow terminated because of permit transfer";

    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final DateService dateService;
    private final WorkflowService workflowService;
    private final ALRCreationService alrCreationService;
    private final InstallationAccountUpdateService installationAccountUpdateService;
    private final InstallationAccountQueryService installationAccountQueryService;


    public void process(final String requestId) {

        // check aer liable
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();
        final TransferParty alrLiable = requestPayload.getPermitTransferDetails().getAlrLiable();


        final Request relatedRequest = requestService.findRequestById(requestPayload.getRelatedRequestId());
        final Long relatedAccountId = relatedRequest.getAccountId();
        InstallationAccountDTO accountDTO = installationAccountQueryService.getAccountDTOById(accountId);

        installationAccountUpdateService.updateFaStatus(relatedAccountId, AccountUpdateFaStatusDTO.builder().faStatus(accountDTO.getFaStatus()).build());

        if (alrLiable == null || alrLiable == TransferParty.TRANSFERER) {
            return;
        }

        // check this year's ALR
        // Final year ALR cannot exist, since permit transfer cannot be initiated after a permit surrender or permit revocation.
        final int alrYear = dateService.getLocalDateTime().getYear();

        final List<Request> alrsCompleted =
                requestRepository.findByAccountIdAndTypeInAndStatus(accountId, List.of(RequestType.ALR), RequestStatus.COMPLETED);
        final Optional<Request> alrCompletedOpt = alrsCompleted.stream()
                .filter(r -> ((ALRRequestMetaData) r.getMetadata()).getYear().getValue() == alrYear).findFirst();

        //Completed ALR - Do Nothing
        if (alrCompletedOpt.isPresent()) {
            return;
        }
        final List<Request> alrsInProgress =
                requestRepository.findByAccountIdAndTypeInAndStatus(accountId, List.of(RequestType.ALR), RequestStatus.IN_PROGRESS);
        final Optional<Request> alrInProgressOpt = alrsInProgress.stream()
                .filter(r -> ((ALRRequestMetaData) r.getMetadata()).getYear().getValue() == alrYear).findFirst();

        //In Progress ALR - cancel old and create new for the receiver
        if (alrInProgressOpt.isPresent()) {
            final Request currentAlr = alrInProgressOpt.get();

            // close alr for transferer
            this.closeTransfererAlr(currentAlr);

            // start alr for receiver
            this.startReceiverAlr(requestPayload.getRelatedRequestId());
        }
    }

    private void closeTransfererAlr(final Request request) {

        workflowService.deleteProcessInstance(request.getProcessInstanceId(), DELETE_REASON);
        request.setStatus(RequestStatus.CANCELLED);
        requestService.addActionToRequest(
                request,
                null,
                RequestActionType.REQUEST_TERMINATED,
                null
        );
    }

    private void startReceiverAlr(String relatedRequestId) {

        final Request relatedRequest = requestService.findRequestById(relatedRequestId);
        final Long relatedAccountId = relatedRequest.getAccountId();
        alrCreationService.createALR(relatedAccountId, false, Optional.empty());
    }
}