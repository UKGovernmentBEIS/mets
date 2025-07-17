package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermitTransferAAerService {

    private static final String DELETE_REASON = "Aer workflow terminated because of permit transfer";
    
    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final DateService dateService;
    private final WorkflowService workflowService;
    private final AerCreationService aerCreationService;

    public void process(final String requestId) {
        
        // check aer liable
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();
        final TransferParty aerLiable = requestPayload.getPermitTransferDetails().getAerLiable();
        if (aerLiable == TransferParty.TRANSFERER) {
            return;
        }
        
        // check last year aer in progress
        final int lastYear = dateService.getLocalDateTime().getYear() - 1;
        final List<Request> aersInProgress =
            requestRepository.findByAccountIdAndTypeInAndStatus(accountId, List.of(RequestType.AER), RequestStatus.IN_PROGRESS);
        final Optional<Request> lastYearAerOpt = aersInProgress.stream()
            .filter(r -> ((AerRequestMetadata) r.getMetadata()).getYear().getValue() == lastYear).findFirst();
        if (lastYearAerOpt.isEmpty()) {
            return;
        }
        
        final Request lastYearAer = lastYearAerOpt.get();
        
        // close aer for transferer
        this.closeTransfererAer(lastYearAer);
        
        // start aer for receiver
        this.startReceiverAer(lastYearAer, requestPayload.getRelatedRequestId());
    }

    private void closeTransfererAer(final Request request) {

        workflowService.deleteProcessInstance(request.getProcessInstanceId(), DELETE_REASON);
        request.setStatus(RequestStatus.CANCELLED);
        requestService.addActionToRequest(
            request,
            null,
            RequestActionType.REQUEST_TERMINATED,
            null
        );
    }

    private void startReceiverAer(final Request lastYearAerRequest, final String relatedRequestId) {

        final Request relatedRequest = requestService.findRequestById(relatedRequestId);
        final Long relatedAccountId = relatedRequest.getAccountId();
        aerCreationService.createRequestAer(relatedAccountId, lastYearAerRequest.getType());
    }
}
