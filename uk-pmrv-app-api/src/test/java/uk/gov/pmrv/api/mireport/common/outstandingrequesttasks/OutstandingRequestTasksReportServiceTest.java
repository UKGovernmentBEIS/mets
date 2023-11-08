package uk.gov.pmrv.api.mireport.common.outstandingrequesttasks;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestTaskViewService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OutstandingRequestTasksReportServiceTest {

    @InjectMocks
    private OutstandingRequestTasksReportService service;

    @Mock
    private RequestTaskViewService requestTaskViewService;

    @Test
    public void getRequestTasks() {
        final String user = "user";
        final PmrvUser pmrvUser = PmrvUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleType.REGULATOR).build();
        Set<RequestTaskType> expectedRequestTaskTypes = Set.of(
            RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS,
            RequestTaskType.PERMIT_ISSUANCE_TRACK_PAYMENT
        );

        when(requestTaskViewService.getRequestTaskTypes(RoleType.REGULATOR)).thenReturn(expectedRequestTaskTypes);

        Set<RequestTaskType> actualRequestTasks =
            service.getRequestTaskTypesByRoleTypeAndAccountType(pmrvUser.getRoleType(), AccountType.INSTALLATION);

        Assertions.assertThat(actualRequestTasks.size()).isEqualTo(expectedRequestTaskTypes.size() - 2);
        Assertions.assertThat(actualRequestTasks).containsAll(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW));
    }
}
