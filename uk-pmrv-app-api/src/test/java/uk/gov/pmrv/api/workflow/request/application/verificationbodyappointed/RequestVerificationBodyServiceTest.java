package uk.gov.pmrv.api.workflow.request.application.verificationbodyappointed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestVerificationBodyServiceTest {
    
    @InjectMocks
    private RequestVerificationBodyService service;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestService requestService;

    @Test
    void appointVerificationBodyToRequestsOfAccount() {
        Long accountId = 1L;
        Long vbIdOld = 11L;
        Long vbIdNew = 21L;
        String verifierAssignee = "verifierAssignee";
        Request request1 = Request.builder()
            .type(RequestType.PERMIT_ISSUANCE)
            .accountId(accountId)
            .verificationBodyId(vbIdOld)
            .status(RequestStatus.APPROVED)
            .build();
        Request request2 = Request.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .accountId(accountId)
            .verificationBodyId(vbIdOld)
            .status(RequestStatus.IN_PROGRESS)
            .payload(PermitSurrenderRequestPayload.builder().verifierAssignee(verifierAssignee).build())
            .build();

        when(requestRepository.findAllByAccountIdAndTypeIsNot(accountId, RequestType.SYSTEM_MESSAGE_NOTIFICATION))
            .thenReturn(List.of(request1, request2));

        service.appointVerificationBodyToRequestsOfAccount(vbIdNew, accountId);

        assertEquals(request1.getVerificationBodyId(), vbIdNew);
        assertEquals(request2.getVerificationBodyId(), vbIdNew);
        assertNull(request2.getPayload().getVerifierAssignee());
        verifyNoInteractions(workflowService);
    }

    @Test
    void appointVerificationBodyToRequestsOfAccount_throw_error() {
        Long accountId = 1L;
        Long vbIdOld = 11L;
        Long vbIdNew = 21L;
        String verifierAssignee = "verifierAssignee";
        Request request1 = Request.builder()
                .type(RequestType.PERMIT_ISSUANCE)
                .accountId(accountId)
                .verificationBodyId(vbIdOld)
                .status(RequestStatus.APPROVED)
                .requestTasks(List.of(
                        RequestTask.builder().id(1L).type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW).build(),
                        RequestTask.builder().id(2L).type(RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_REVIEW).build()
                ))
                .build();
        Request request2 = Request.builder()
                .type(RequestType.AER)
                .accountId(accountId)
                .verificationBodyId(vbIdOld)
                .status(RequestStatus.IN_PROGRESS)
                .payload(PermitSurrenderRequestPayload.builder().verifierAssignee(verifierAssignee).build())
                .requestTasks(List.of(
                        RequestTask.builder().id(3L).type(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT).build(),
                        RequestTask.builder().id(4L).type(RequestTaskType.AER_WAIT_FOR_VERIFICATION).build()
                ))
                .build();

        when(requestRepository.findAllByAccountIdAndTypeIsNot(accountId, RequestType.SYSTEM_MESSAGE_NOTIFICATION))
                .thenReturn(List.of(request1, request2));

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.appointVerificationBodyToRequestsOfAccount(vbIdNew, accountId));

        assertEquals(ErrorCode.VERIFICATION_RELATED_REQUEST_TASKS_EXIST_FOR_ACCOUNT, businessException.getErrorCode());
        assertThat(businessException.getData()).containsOnly(3L);
        verify(requestRepository, never()).saveAll(anyCollection());
        verifyNoInteractions(workflowService);
    }

    @Test
    void unappointVerificationBodyFromRequestsOfAccounts() {
        Long accountId = 1L;
        Long verificationBodyId = 11L;
        String verifierAssignee = "verifierAssignee";
        Request request1 = Request.builder()
            .type(RequestType.PERMIT_ISSUANCE)
            .accountId(accountId)
            .verificationBodyId(verificationBodyId)
            .status(RequestStatus.APPROVED)
            .build();
        Request request2 = Request.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .accountId(accountId)
            .verificationBodyId(verificationBodyId)
            .status(RequestStatus.IN_PROGRESS)
            .payload(PermitSurrenderRequestPayload.builder().verifierAssignee(verifierAssignee).build())
            .build();

        when(requestRepository.findAllByAccountIdInAndTypeIsNot(Set.of(accountId), RequestType.SYSTEM_MESSAGE_NOTIFICATION))
            .thenReturn(List.of(request1, request2));

        service.unappointVerificationBodyFromRequestsOfAccounts(Set.of(accountId));

        assertNull(request1.getVerificationBodyId());
        assertNull(request2.getVerificationBodyId());
        assertNull(request2.getPayload().getVerifierAssignee());
        verifyNoInteractions(workflowService);
        verifyNoInteractions(requestService);
    }

    @Test
    void unappointVerificationBodyFromRequestsOfAccounts_with_open_tasks() {
        Long accountId = 1L;
        Long verificationBodyId = 11L;
        String verifierAssignee = "verifierAssignee";
        Request request1 = Request.builder()
                .id("request1")
                .type(RequestType.AER)
                .accountId(accountId)
                .verificationBodyId(verificationBodyId)
                .status(RequestStatus.IN_PROGRESS)
                .requestTasks(List.of(
                        RequestTask.builder().type(RequestTaskType.AER_WAIT_FOR_VERIFICATION).build(),
                        RequestTask.builder().type(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT).build()
                ))
                .build();
        Request request2 = Request.builder()
                .id("request2")
                .type(RequestType.AER)
                .accountId(accountId)
                .verificationBodyId(verificationBodyId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(PermitSurrenderRequestPayload.builder().verifierAssignee(verifierAssignee).build())
                .requestTasks(List.of(
                        RequestTask.builder().type(RequestTaskType.AER_APPLICATION_SUBMIT).build()
                ))
                .build();

        when(requestRepository.findAllByAccountIdInAndTypeIsNot(Set.of(accountId), RequestType.SYSTEM_MESSAGE_NOTIFICATION))
                .thenReturn(List.of(request1, request2));

        service.unappointVerificationBodyFromRequestsOfAccounts(Set.of(accountId));

        assertNull(request1.getVerificationBodyId());
        assertNull(request2.getVerificationBodyId());
        assertNull(request2.getPayload().getVerifierAssignee());
        verify(workflowService, times(1))
                .sendEvent(request1.getId(), BpmnProcessConstants.VERIFICATION_BODY_STATE_CHANGED, Map.of());
        verify(requestService, times(1))
                .addActionToRequest(request1, null, RequestActionType.VERIFICATION_STATEMENT_CANCELLED, null);
        verifyNoMoreInteractions(workflowService);
    }
}
