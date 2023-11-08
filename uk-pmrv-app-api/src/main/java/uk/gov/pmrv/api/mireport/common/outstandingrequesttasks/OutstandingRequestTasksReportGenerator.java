package uk.gov.pmrv.api.mireport.common.outstandingrequesttasks;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class OutstandingRequestTasksReportGenerator {

    private final OutstandingRequestTasksReportService outstandingRequestTasksReportService;
    private final OutstandingRequestTasksRepository outstandingRequestTasksRepository;
    private final UserAuthService userAuthService;

    public abstract AccountType getAccountType();

    public MiReportType getReportType() {
        return MiReportType.REGULATOR_OUTSTANDING_REQUEST_TASKS;
    }


    public MiReportResult generateMiReport(EntityManager entityManager, OutstandingRegulatorRequestTasksMiReportParams reportParams) {
        Set<RequestTaskType> regulatorRequestTaskTypes =
            outstandingRequestTasksReportService.getRequestTaskTypesByRoleTypeAndAccountType(RoleType.REGULATOR, getAccountType());
        reportParams.getRequestTaskTypes().retainAll(regulatorRequestTaskTypes);

        if (reportParams.getRequestTaskTypes().isEmpty()) {
            reportParams.setRequestTaskTypes(regulatorRequestTaskTypes);
        }

        List<OutstandingRequestTask> outstandingRequestTasks =
            outstandingRequestTasksRepository.findOutstandingRequestTaskParams(entityManager, reportParams);

        this.resolveAssigneeNames(outstandingRequestTasks);

        return OutstandingRequestTasksMiReportResult.builder()
            .reportType(getReportType())
            .columnNames(OutstandingRequestTask.getColumnNames())
            .results(outstandingRequestTasks)
            .build();
    }

    private void resolveAssigneeNames(final List<OutstandingRequestTask> outstandingRequestTasks) {

        final List<String> assigneeIds = outstandingRequestTasks.stream()
            .map(OutstandingRequestTask::getRequestTaskAssignee)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        final List<UserInfo> assignees = userAuthService.getUsers(assigneeIds);
        final Map<String, String> assigneeNames = assignees.stream()
            .collect(Collectors.toMap(
                    UserInfo::getId,
                    ui -> ui.getFirstName() + " " + ui.getLastName()
                )
            );
        outstandingRequestTasks.stream()
            .filter(ort -> ort.getRequestTaskAssignee() != null)
            .forEach(
                ort -> ort.setRequestTaskAssigneeName(assigneeNames.get(ort.getRequestTaskAssignee()))
            );
    }
}
