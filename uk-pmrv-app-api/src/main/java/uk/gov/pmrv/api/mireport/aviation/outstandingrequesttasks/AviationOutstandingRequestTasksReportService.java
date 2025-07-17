package uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.RequestTaskTypeFilter;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestTaskViewService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationOutstandingRequestTasksReportService implements OutstandingRequestTasksReportService {

    private final RequestTaskViewService requestTaskViewService;

    @Transactional(readOnly = true)
    public Set<RequestTaskType> getRequestTaskTypesByRoleTypeAndAccountType(String roleType, AccountType accountType) {
        return requestTaskViewService.getRequestTaskTypes(roleType).stream()
                .filter(requestTaskType -> accountType.equals(requestTaskType.getRequestType().getAccountType()) &&
                        !RequestTaskTypeFilter.containsExcludedRequestTaskType(requestTaskType))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRequestTaskTypesByRoleType(String roleType) {
        return getRequestTaskTypesByRoleTypeAndAccountType(roleType, AccountType.AVIATION)
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
