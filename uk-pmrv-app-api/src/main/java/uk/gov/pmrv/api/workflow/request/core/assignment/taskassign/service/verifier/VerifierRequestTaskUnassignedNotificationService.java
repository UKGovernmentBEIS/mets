package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskUnassignedNotificationService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UnassignedTaskNotificationService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerifierRequestTaskUnassignedNotificationService implements RequestTaskUnassignedNotificationService {

    private final UnassignedTaskNotificationService unassignedTaskNotificationService;

    @Override
    public void requestTaskUnassignedTaskNotification(RequestTask requestTask) {
        unassignedTaskNotificationService.sendUnassignedTaskNotificationService(requestTask);
    }

    @Override
    public String getRoleType() {
        return RoleTypeConstants.VERIFIER;
    }
}
