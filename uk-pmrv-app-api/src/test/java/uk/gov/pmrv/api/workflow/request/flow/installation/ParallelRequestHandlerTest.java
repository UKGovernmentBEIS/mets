package uk.gov.pmrv.api.workflow.request.flow.installation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_REVOCATION;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_SURRENDER;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_TRANSFER_A;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_TRANSFER_B;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_VARIATION;

@ExtendWith(MockitoExtension.class)
class ParallelRequestHandlerTest {

    private static final String DELETE_REASON = "Workflow terminated by the system";
    private static final Long TEST_ACCOUNT_ID = 1L;
    private static final String TEST_PROCESS_INSTANCE_ID = "1";

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private WorkflowService workflowService;
    @Mock
    private RequestService requestService;
    @Mock
    private RequestQueryService requestQueryService;


    @InjectMocks
    ParallelRequestHandler cut;

    @Test
    void shouldClosePermitRevocationOnSurrender() {

        List<Request> requests = List.of(
            Request.builder()
                .processInstanceId(TEST_PROCESS_INSTANCE_ID)
                .type(PERMIT_REVOCATION)
                .status(RequestStatus.IN_PROGRESS)
                .build()
        );
        when(requestRepository.findByAccountIdAndTypeInAndStatus(TEST_ACCOUNT_ID,
            List.of(PERMIT_REVOCATION, PERMIT_TRANSFER_A, PERMIT_VARIATION), RequestStatus.IN_PROGRESS)).thenReturn(
            requests);

        cut.handleParallelRequests(TEST_ACCOUNT_ID, InstallationAccountStatus.AWAITING_SURRENDER);

        verify(workflowService, times(1)).deleteProcessInstance(TEST_PROCESS_INSTANCE_ID,
            DELETE_REASON);
        verify(requestService, times(1)).addActionToRequest(requests.get(0), null, RequestActionType.REQUEST_TERMINATED,
            null);
        assertThat(requests.get(0).getStatus()).isEqualTo(RequestStatus.CANCELLED);
    }
    
    @Test
    void shouldClosePermitSurrenderOnRevocation() {

        List<Request> requests = List.of(
            Request.builder()
                .processInstanceId(TEST_PROCESS_INSTANCE_ID)
                .type(PERMIT_SURRENDER)
                .status(RequestStatus.IN_PROGRESS)
                .build()
        );
        when(requestRepository.findByAccountIdAndTypeInAndStatus(TEST_ACCOUNT_ID,
            List.of(PERMIT_SURRENDER, PERMIT_TRANSFER_A, PERMIT_VARIATION), RequestStatus.IN_PROGRESS)).thenReturn(
            requests);

        cut.handleParallelRequests(TEST_ACCOUNT_ID, InstallationAccountStatus.AWAITING_REVOCATION);

        verify(workflowService, times(1)).deleteProcessInstance(TEST_PROCESS_INSTANCE_ID,
            DELETE_REASON);
        verify(requestService, times(1)).addActionToRequest(requests.get(0), null, RequestActionType.REQUEST_TERMINATED,
            null);
        assertThat(requests.get(0).getStatus()).isEqualTo(RequestStatus.CANCELLED);
    }

    @Test
    void shouldClosePermitRevocationOnTransfer() {

        List<Request> requests = List.of(
            Request.builder()
                .processInstanceId(TEST_PROCESS_INSTANCE_ID)
                .type(PERMIT_REVOCATION)
                .status(RequestStatus.IN_PROGRESS)
                .build()
        );
        when(requestRepository.findByAccountIdAndTypeInAndStatus(TEST_ACCOUNT_ID,
            List.of(PERMIT_REVOCATION), RequestStatus.IN_PROGRESS)).thenReturn(
            requests);

        when(requestRepository.findByAccountIdAndTypeInAndStatus(TEST_ACCOUNT_ID,
            List.of(PERMIT_REVOCATION), RequestStatus.IN_PROGRESS)).thenReturn(
            requests);

        cut.handleParallelRequests(TEST_ACCOUNT_ID, InstallationAccountStatus.TRANSFERRED);

        verify(workflowService, times(1)).deleteProcessInstance(TEST_PROCESS_INSTANCE_ID,
            DELETE_REASON);
        verify(requestService, times(1)).addActionToRequest(requests.get(0), null, RequestActionType.REQUEST_TERMINATED,
            null);
        assertThat(requests.get(0).getStatus()).isEqualTo(RequestStatus.CANCELLED);
    }

    @Test
    void shouldCloseLinkedRequests() {

        final String linkedRequestId = "linkedRequestId";
        final String linkedProcessInstanceId = "linkedProcessInstanceId";
        
        final List<Request> requests = List.of(
            Request.builder()
                .processInstanceId(TEST_PROCESS_INSTANCE_ID)
                .type(PERMIT_TRANSFER_A)
                .payload(PermitTransferARequestPayload.builder().relatedRequestId(linkedRequestId).build())
                .status(RequestStatus.IN_PROGRESS)
                .build()
        );

        final Request linkedRequest = Request.builder()
            .id(linkedRequestId)
            .processInstanceId(linkedProcessInstanceId)
            .type(PERMIT_TRANSFER_B)
            .status(RequestStatus.IN_PROGRESS)
            .build();

        when(requestRepository.findByAccountIdAndTypeInAndStatus(TEST_ACCOUNT_ID, List.of(PERMIT_REVOCATION), RequestStatus.IN_PROGRESS))
            .thenReturn(requests);

        when(requestQueryService.getRelatedRequests(requests)).thenReturn(List.of(linkedRequest));

        cut.handleParallelRequests(TEST_ACCOUNT_ID, InstallationAccountStatus.TRANSFERRED);

        verify(workflowService, times(1)).deleteProcessInstance(TEST_PROCESS_INSTANCE_ID, DELETE_REASON);
        verify(requestService, times(1)).addActionToRequest(requests.get(0), null, RequestActionType.REQUEST_TERMINATED, null);
        
        verify(workflowService, times(1)).deleteProcessInstance(linkedProcessInstanceId, DELETE_REASON);
        verify(requestService, times(1)).addActionToRequest(linkedRequest, null, RequestActionType.REQUEST_TERMINATED, null);

        assertThat(requests.get(0).getStatus()).isEqualTo(RequestStatus.CANCELLED);
        assertThat(linkedRequest.getStatus()).isEqualTo(RequestStatus.CANCELLED);
    }
}
