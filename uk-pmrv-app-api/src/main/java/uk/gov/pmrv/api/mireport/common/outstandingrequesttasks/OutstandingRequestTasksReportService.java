package uk.gov.pmrv.api.mireport.common.outstandingrequesttasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestTaskViewService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutstandingRequestTasksReportService {

    private final RequestTaskViewService requestTaskViewService;

    @Transactional(readOnly = true)
    public Set<RequestTaskType> getRequestTaskTypesByRoleTypeAndAccountType(RoleType roleType, AccountType accountType) {
        return requestTaskViewService.getRequestTaskTypes(roleType).stream()
            .filter(requestTaskType -> accountType.equals(requestTaskType.getRequestType().getAccountType()) &&
                !RequestTaskTypeFilter.containsExcludedRequestTaskType(requestTaskType))
            .collect(Collectors.toSet());
    }
}
