package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

public interface RequestTaskUnassignedNotificationService {

    void requestTaskUnassignedTaskNotification(RequestTask requestTask);

    String getRoleType();
}
