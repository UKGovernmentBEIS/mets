package uk.gov.pmrv.api.workflow.request.flow.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_REVOCATION;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_SURRENDER;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_TRANSFER_A;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_VARIATION;

@Service
@RequiredArgsConstructor
public class ParallelRequestHandler {

    private static final String DELETE_REASON = "Workflow terminated by the system";

    private final RequestRepository requestRepository;
    private final WorkflowService workflowService;
    private final RequestService requestService;
    private final RequestQueryService requestQueryService;

    @Transactional
    public void handleParallelRequests(Long accountId, InstallationAccountStatus status) {
        switch (status) {
            case AWAITING_SURRENDER:
                handle(accountId, List.of(PERMIT_REVOCATION, PERMIT_TRANSFER_A, PERMIT_VARIATION));
                break;
            case AWAITING_REVOCATION:
                handle(accountId, List.of(PERMIT_SURRENDER, PERMIT_TRANSFER_A, PERMIT_VARIATION));
                break;
            case TRANSFERRED:
                handle(accountId, List.of(PERMIT_REVOCATION));
                break;
            default:
                break;
        }
    }

    private void handle(Long accountId, List<RequestType> types) {

        final List<Request> sameAccountRequests = this.getAccountRequests(accountId, types);
        final List<Request> otherAccountRelatedRequests = requestQueryService.getRelatedRequests(sameAccountRequests);

        Stream.concat(sameAccountRequests.stream(), otherAccountRelatedRequests.stream()).collect(Collectors.toSet()).forEach(
            this::closeRequestWithAction
        );
    }
    
    private List<Request> getAccountRequests(final Long accountId, final List<RequestType> types) {

        return requestRepository.findByAccountIdAndTypeInAndStatus(
                accountId,
                types,
                RequestStatus.IN_PROGRESS
            );
    }

    private void closeRequestWithAction(Request request) {
        workflowService.deleteProcessInstance(request.getProcessInstanceId(), DELETE_REASON);

        request.setStatus(RequestStatus.CANCELLED);

        requestService.addActionToRequest(
            request,
            null,
            RequestActionType.REQUEST_TERMINATED,
            null
        );
    }
}
