package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
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
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }

    @Transactional
    public void assignDefaultAssigneeToTask(RequestTask requestTask) {
        String requestAssignee = requestTask.getRequest().getPayload().getOperatorAssignee();

        if(!ObjectUtils.isEmpty(requestAssignee) && userRoleTypeService.isUserOperator(requestAssignee)){
            try {
                requestTaskAssignmentService.assignToUser(requestTask, requestAssignee);
            } catch (BusinessCheckedException e) {
                assignTaskToAccountPrimaryContactOrReleaseRequest(requestTask);
            }
        } else {
            assignTaskToAccountPrimaryContactOrReleaseRequest(requestTask);
        }
    }

    private void assignTaskToAccountPrimaryContactOrReleaseRequest(RequestTask requestTask) {
        Optional<String> accountPrimaryContactOptional =
            accountContactQueryService.findPrimaryContactByAccount(requestTask.getRequest().getAccountId());

        accountPrimaryContactOptional.ifPresentOrElse(
            primaryContact -> {
                try {
                    requestTaskAssignmentService.assignToUser(requestTask, primaryContact);
                } catch (BusinessCheckedException e) {
                    requestReleaseService.releaseRequest(requestTask);
                }
            },
            () -> requestReleaseService.releaseRequest(requestTask)
        );
    }
}
