package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

public interface UserRoleRequestTaskAssignmentService {

    void assignTask(RequestTask requestTask, String userId);
    String getRoleType();
}
