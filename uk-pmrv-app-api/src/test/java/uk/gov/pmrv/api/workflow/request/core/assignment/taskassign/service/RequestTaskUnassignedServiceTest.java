package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier.VerifierRequestTaskUnassignedNotificationService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RequestTaskUnassignedServiceTest {

    @InjectMocks
    private RequestTaskUnassignedService requestTaskUnassignedService;

    @Mock
    private VerifierRequestTaskUnassignedNotificationService verifierRequestTaskUnassignedNotificationService;

    @BeforeEach
    void setup() {
        requestTaskUnassignedService = new RequestTaskUnassignedService(
            List.of(verifierRequestTaskUnassignedNotificationService));
    }

    @Test
    void unassignedTaskToNotify_verifier() {
        RequestTask requestTask = RequestTask.builder().type(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT).build();

        when(verifierRequestTaskUnassignedNotificationService.getRoleType()).thenReturn(RoleTypeConstants.VERIFIER);

        requestTaskUnassignedService.unassignedTaskToNotify(RoleTypeConstants.VERIFIER, requestTask);

        verify(verifierRequestTaskUnassignedNotificationService).requestTaskUnassignedTaskNotification(requestTask);
    }

    @Test
    void unassignedTaskToNotify_no_role() {
        RequestTask requestTask = RequestTask.builder().type(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT).build();

        when(verifierRequestTaskUnassignedNotificationService.getRoleType()).thenReturn(RoleTypeConstants.VERIFIER);

        requestTaskUnassignedService.unassignedTaskToNotify(null, requestTask);

        verifyNoMoreInteractions(verifierRequestTaskUnassignedNotificationService);
    }
}