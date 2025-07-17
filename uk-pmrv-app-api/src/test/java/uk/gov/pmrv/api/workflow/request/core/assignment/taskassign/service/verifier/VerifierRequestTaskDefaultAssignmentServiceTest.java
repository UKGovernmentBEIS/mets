package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.account.service.AccountVbSiteContactService;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierRequestTaskDefaultAssignmentServiceTest {

    @InjectMocks
    private VerifierRequestTaskDefaultAssignmentService verifierRequestTaskDefaultAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private AccountVbSiteContactService accountVbSiteContactService;

    @Mock
    private RequestReleaseService requestReleaseService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Test
    void assignDefaultAssigneeToTask() throws BusinessCheckedException {
        final String verifier = "verifier";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(AerRequestPayload.builder()
                                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                                .verifierAssignee(verifier)
                                .build())
                        .build())
                .build();

        when(userRoleTypeService.isUserVerifier(verifier)).thenReturn(true);

        // Invoke
        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        // Verify
        verify(userRoleTypeService, times(1)).isUserVerifier(verifier);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, verifier);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_no_verifier() throws BusinessCheckedException {
        final String verifier = "verifier";
        final String vbSiteContact = "vbSiteContact";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .accountId(1L)
                        .payload(AerRequestPayload.builder()
                                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                                .verifierAssignee(verifier)
                                .build())
                        .build())
                .build();

        when(userRoleTypeService.isUserVerifier(verifier)).thenReturn(false);
        when(accountVbSiteContactService.getVBSiteContactByAccount(requestTask.getRequest().getAccountId()))
                .thenReturn(Optional.of(vbSiteContact));

        // Invoke
        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        // Verify
        verify(userRoleTypeService, times(1)).isUserVerifier(verifier);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, vbSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_no_verifier_throw_exception() throws BusinessCheckedException {
        final String verifier = "verifier";
        final String vbSiteContact = "vbSiteContact";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .accountId(1L)
                        .payload(AerRequestPayload.builder()
                                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                                .verifierAssignee(verifier)
                                .build())
                        .build())
                .build();

        when(userRoleTypeService.isUserVerifier(verifier)).thenReturn(false);
        when(accountVbSiteContactService.getVBSiteContactByAccount(requestTask.getRequest().getAccountId()))
                .thenReturn(Optional.of(vbSiteContact));
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, vbSiteContact);

        // Invoke
        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        // Verify
        verify(userRoleTypeService, times(1)).isUserVerifier(verifier);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, vbSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
    }

    @Test
    void assignDefaultAssigneeToTask_no_assignee() {
        final String verifier = "verifier";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .accountId(1L)
                        .payload(AerRequestPayload.builder()
                                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                                .verifierAssignee(verifier)
                                .build())
                        .build())
                .build();

        when(userRoleTypeService.isUserVerifier(verifier)).thenReturn(false);
        when(accountVbSiteContactService.getVBSiteContactByAccount(requestTask.getRequest().getAccountId()))
                .thenReturn(Optional.empty());

        // Invoke
        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        // Verify
        verify(userRoleTypeService, times(1)).isUserVerifier(verifier);
        verifyNoInteractions(requestTaskAssignmentService);
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
    }

    @Test
    void assignDefaultAssigneeToTask_throw_exception() throws BusinessCheckedException {
        final String verifier = "verifier";
        final String vbSiteContact = "vbSiteContact";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .accountId(1L)
                        .payload(AerRequestPayload.builder()
                                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                                .verifierAssignee(verifier)
                                .build())
                        .build())
                .build();

        when(userRoleTypeService.isUserVerifier(verifier)).thenReturn(true);
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, verifier);
        when(accountVbSiteContactService.getVBSiteContactByAccount(requestTask.getRequest().getAccountId()))
                .thenReturn(Optional.of(vbSiteContact));

        // Invoke
        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        // Verify
        verify(userRoleTypeService, times(1)).isUserVerifier(verifier);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, verifier);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, vbSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void getRoleType() {
        Assertions.assertEquals(RoleTypeConstants.VERIFIER, verifierRequestTaskDefaultAssignmentService.getRoleType());
    }
}