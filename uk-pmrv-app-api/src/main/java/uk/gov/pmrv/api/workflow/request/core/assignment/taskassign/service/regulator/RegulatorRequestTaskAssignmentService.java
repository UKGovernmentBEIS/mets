package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.regulator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.SiteContactRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

@Service
@AllArgsConstructor
public class RegulatorRequestTaskAssignmentService implements UserRoleRequestTaskAssignmentService {

    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final SiteContactRequestTaskAssignmentService siteContactRequestTaskAssignmentService;

    @Override
    public String getRoleType() {
        return RoleTypeConstants.REGULATOR;
    }

    @Transactional
    public void assignTask(RequestTask requestTask, String userId) {
        if(requestTask.getType().isPeerReview()) {
            assignPeerReviewTask(requestTask, userId);
        } else {
            assignTaskToUser(requestTask, userId);
        }
    }

    @Transactional
    public void assignTasksOfDeletedRegulatorToCaSiteContactOrRelease(String userDeleted) {
        siteContactRequestTaskAssignmentService
            .assignTasksOfDeletedUserToSiteContactOrRelease(userDeleted, AccountContactType.CA_SITE);
    }

    private void assignPeerReviewTask(RequestTask requestTask, String userId) {
        String firstReviewer = requestTask.getRequest().getPayload().getRegulatorReviewer();
        if(userId.equals(firstReviewer)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        } else {
            assignTaskToUser(requestTask, userId);
        }
    }

    private void assignTaskToUser(RequestTask requestTask, String userId) {
        try {
            requestTaskAssignmentService.assignToUser(requestTask, userId);
        } catch (BusinessCheckedException e) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }
    }
}
