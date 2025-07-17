package uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.mireport.MiReportType;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTask;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTasksMiReportResult;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationOutstandingRequestTasksReportGeneratorHandlerTest {

    @InjectMocks
    private AviationOutstandingRequestTasksReportGeneratorHandler generator;

    @Mock
    private AviationOutstandingRequestTasksRepository outstandingRequestTasksRepository;

    @Mock
    private AviationOutstandingRequestTasksReportService outstandingRequestTasksReportService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private EntityManager entityManager;

    @Captor
    private ArgumentCaptor<OutstandingRegulatorRequestTasksMiReportParams> argumentCaptor;

    @Test
    void generateMiReport() {
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        OutstandingRegulatorRequestTasksMiReportParams params = OutstandingRegulatorRequestTasksMiReportParams.builder()
                .userIds(Set.of(userId1, userId2))
                .requestTaskTypes(
                        new HashSet<>(List.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name(),
                                RequestTaskType.EMP_ISSUANCE_UKETS_WAIT_FOR_RFI_RESPONSE.name(),
                                RequestTaskType.EMP_ISSUANCE_UKETS_TRACK_PAYMENT.name())))
                .build();

        List<AviationOutstandingRequestTask> expectedOutstandingRequestTasks = List.of(
                AviationOutstandingRequestTask.builder()
                        .accountId("emitterId")
                        .accountType(AccountType.AVIATION)
                        .legalEntityName("legal1")
                        .requestId(UUID.randomUUID().toString())
                        .requestType(RequestType.EMP_ISSUANCE_UKETS.name())
                        .requestTaskType(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW.name())
                        .requestTaskAssignee(userId1)
                        .requestTaskAssigneeName("Jon Jones")
                        .requestTaskDueDate(null)
                        .requestTaskRemainingDays(null)
                        .crcoCode("crcoOCde1")
                        .build(),
                AviationOutstandingRequestTask.builder()
                        .accountId("emitterId2")
                        .accountType(AccountType.AVIATION)
                        .legalEntityName("legal2")
                        .requestId(UUID.randomUUID().toString())
                        .requestType(RequestType.EMP_ISSUANCE_UKETS.name())
                        .requestTaskType(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name())
                        .requestTaskAssignee(userId2)
                        .requestTaskAssigneeName("Stan Smith")
                        .requestTaskDueDate(LocalDate.now().plusDays(10))
                        .requestTaskRemainingDays(10L)
                        .crcoCode("crcoOCde2")
                        .build()
        );

        when(outstandingRequestTasksReportService.getRequestTaskTypesByRoleType(RoleTypeConstants.REGULATOR))
                .thenReturn(Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name()));

        when(outstandingRequestTasksRepository.findOutstandingRequestTaskParams(any(), argumentCaptor.capture())).thenReturn(expectedOutstandingRequestTasks);
        when(userAuthService.getUsers(List.of(userId1, userId2))).thenReturn(
                List.of(
                        UserInfo.builder().id(userId1).firstName("Jon").lastName("Jones").build(),
                        UserInfo.builder().id(userId2).firstName("Stan").lastName("Smith").build()
                )
        );

        OutstandingRequestTasksMiReportResult<AviationOutstandingRequestTask> miReportResult = (OutstandingRequestTasksMiReportResult) generator.generateMiReport(entityManager, params);

        assertThat(miReportResult.getResults()).hasSize(2);
        assertThat(argumentCaptor.getValue().getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name());
        assertThat(miReportResult.getResults()).containsExactlyElementsOf(expectedOutstandingRequestTasks);
        assertThat(miReportResult.getReportType()).isEqualTo(MiReportType.REGULATOR_OUTSTANDING_REQUEST_TASKS);
    }

    @Test
    void generateMiReport_all_request_tasks() {
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        OutstandingRegulatorRequestTasksMiReportParams params = OutstandingRegulatorRequestTasksMiReportParams.builder()
                .userIds(Set.of(userId1, userId2))
                .requestTaskTypes(new HashSet<>())
                .build();

        List<AviationOutstandingRequestTask> expectedOutstandingRequestTasks = List.of(
                AviationOutstandingRequestTask.builder()
                        .accountId("emitterId")
                        .accountType(AccountType.AVIATION)
                        .legalEntityName("legal1")
                        .requestId(UUID.randomUUID().toString())
                        .requestType(RequestType.EMP_ISSUANCE_UKETS.name())
                        .requestTaskType(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name())
                        .requestTaskAssignee(userId1)
                        .requestTaskAssigneeName("Jon Jones")
                        .requestTaskDueDate(null)
                        .requestTaskRemainingDays(null)
                        .crcoCode("crcoCode1")
                        .build(),
                AviationOutstandingRequestTask.builder()
                        .accountId("emitterId2")
                        .accountType(AccountType.AVIATION)
                        .legalEntityName("legal2")
                        .requestId(UUID.randomUUID().toString())
                        .requestType(RequestType.EMP_ISSUANCE_UKETS.name())
                        .requestTaskType(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW.name())
                        .requestTaskAssignee(userId2)
                        .requestTaskAssigneeName("Stan Smith")
                        .requestTaskDueDate(LocalDate.now().plusDays(10))
                        .requestTaskRemainingDays(10L)
                        .crcoCode("crcoCode2")
                        .build()
        );

        when(outstandingRequestTasksReportService.getRequestTaskTypesByRoleType(RoleTypeConstants.REGULATOR))
                .thenReturn(Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name()));

        when(outstandingRequestTasksRepository.findOutstandingRequestTaskParams(any(), argumentCaptor.capture())).thenReturn(expectedOutstandingRequestTasks);
        when(userAuthService.getUsers(List.of(userId1, userId2))).thenReturn(
                List.of(
                        UserInfo.builder().id(userId1).firstName("Jon").lastName("Jones").build(),
                        UserInfo.builder().id(userId2).firstName("Stan").lastName("Smith").build()
                )
        );

        OutstandingRequestTasksMiReportResult<AviationOutstandingRequestTask> miReportResult = (OutstandingRequestTasksMiReportResult) generator.generateMiReport(entityManager, params);

        assertThat(miReportResult.getResults()).hasSize(2);
        assertThat(argumentCaptor.getValue().getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name());
        assertThat(miReportResult.getResults()).containsExactlyElementsOf(expectedOutstandingRequestTasks);
        assertThat(miReportResult.getReportType()).isEqualTo(MiReportType.REGULATOR_OUTSTANDING_REQUEST_TASKS);
    }

    @Test
    void getColumnNames() {
        assertThat(generator.getColumnNames()).containsExactlyElementsOf(AviationOutstandingRequestTask.getColumnNames());
    }
}
