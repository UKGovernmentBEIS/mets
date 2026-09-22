package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperatorRequestTaskDefaultAssignmentService implements UserRoleRequestTaskDefaultAssignmentService {

    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final AccountContactQueryService accountContactQueryService;
    private final UserRoleTypeService userRoleTypeService;
    private final RequestReleaseService requestReleaseService;

    @Override
    public String getRoleType() {
        return RoleTypeConstants.OPERATOR;
    }

    @Transactional
    public void assignDefaultAssigneeToTask(RequestTask requestTask) {
        String requestAssignee = requestTask.getRequest().getPayload().getOperatorAssignee();

        if (isExistingUserOfRoleType(requestAssignee)) {
            try {
                requestTaskAssignmentService.assignToUser(requestTask, requestAssignee, getRoleType());
            } catch (BusinessCheckedException e) {
                assignTaskToAccountPrimaryContactOrReleaseRequest(requestTask);
            }
        } else {
            assignTaskToAccountPrimaryContactOrReleaseRequest(requestTask);
        }
    }

    /**
     * The request payload holds the assignee of the last time the request was open, so by the time a closed request
     * is re-opened that user may have been deleted. A deleted user has no role type any more, in which case the task
     * falls back to the account primary contact instead of failing.
     */
    private boolean isExistingUserOfRoleType(String userId) {
        return !ObjectUtils.isEmpty(userId)
                && userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)
                .map(UserRoleTypeDTO::getRoleType)
                .filter(getRoleType()::equals)
                .isPresent();
    }

    private void assignTaskToAccountPrimaryContactOrReleaseRequest(RequestTask requestTask) {
        Optional<String> accountPrimaryContactOptional =
            accountContactQueryService.findPrimaryContactByAccount(requestTask.getRequest().getAccountId());

        accountPrimaryContactOptional.ifPresentOrElse(
            primaryContact -> {
                try {
                    requestTaskAssignmentService.assignToUser(requestTask, primaryContact, getRoleType());
                } catch (BusinessCheckedException e) {
                    requestReleaseService.releaseRequest(requestTask);
                }
            },
            () -> requestReleaseService.releaseRequest(requestTask)
        );
    }
}
