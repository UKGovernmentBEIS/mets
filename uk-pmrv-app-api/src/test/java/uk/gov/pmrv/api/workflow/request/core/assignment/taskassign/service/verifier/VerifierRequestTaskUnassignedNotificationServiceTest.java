package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UnassignedTaskNotificationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerifierRequestTaskUnassignedNotificationServiceTest {

    @InjectMocks
    private VerifierRequestTaskUnassignedNotificationService service;

    @Mock
    private UnassignedTaskNotificationService unassignedTaskNotificationService;

    @Test
    void requestTaskUnassignedTaskNotification() {
        Request request = new Request();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .build();

        service.requestTaskUnassignedTaskNotification(requestTask);

        verify(unassignedTaskNotificationService).sendUnassignedTaskNotificationService(requestTask);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleTypeConstants.VERIFIER, service.getRoleType());
    }
}