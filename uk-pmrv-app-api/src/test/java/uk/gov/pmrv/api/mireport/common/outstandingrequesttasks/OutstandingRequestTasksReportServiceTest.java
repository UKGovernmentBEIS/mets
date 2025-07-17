package uk.gov.pmrv.api.mireport.common.outstandingrequesttasks;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks.AviationOutstandingRequestTasksReportService;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestTaskViewService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OutstandingRequestTasksReportServiceTest {

    @InjectMocks
    private AviationOutstandingRequestTasksReportService service;

    @Mock
    private RequestTaskViewService requestTaskViewService;

    @Test
    public void getRequestTasks() {
        final String user = "user";
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleTypeConstants.REGULATOR).build();
        Set<RequestTaskType> expectedRequestTaskTypes = Set.of(
            RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS,
            RequestTaskType.PERMIT_ISSUANCE_TRACK_PAYMENT
        );

        when(requestTaskViewService.getRequestTaskTypes(RoleTypeConstants.REGULATOR)).thenReturn(expectedRequestTaskTypes);

        Set<RequestTaskType> actualRequestTasks =
            service.getRequestTaskTypesByRoleTypeAndAccountType(appUser.getRoleType(), AccountType.INSTALLATION);

        Assertions.assertThat(actualRequestTasks.size()).isEqualTo(expectedRequestTaskTypes.size() - 2);
        Assertions.assertThat(actualRequestTasks).containsAll(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW));
    }
}
