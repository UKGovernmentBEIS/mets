package uk.gov.pmrv.api.mireport.installation.executedactions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestAction;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationExecutedRequestActionsReportGeneratorHandlerTest {

    @InjectMocks
    private InstallationExecutedRequestActionsReportGeneratorHandler generator;

    @Mock
    private InstallationExecutedRequestActionsRepository repository;

    @Mock
    private EntityManager entityManager;

    @Test
    void generateMiReport() {
        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.now())
            .build();
        List<ExecutedRequestAction> executedRequestActions = List.of(
            ExecutedRequestAction.builder()
                .emitterId("emitterId")
                .accountName("accountName")
                .accountStatus(InstallationAccountStatus.LIVE.name())
                .accountType(AccountType.INSTALLATION)
                .legalEntityName("legalEntityName")
                .requestId("REQ-1")
                .requestType(RequestType.PERMIT_ISSUANCE)
                .requestStatus(RequestStatus.IN_PROGRESS)
                .requestActionType(RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED)
                .requestActionSubmitter("submitter")
                .requestActionCompletionDate(LocalDateTime.now())
                .build());

        when(repository.findExecutedRequestActions(entityManager, reportParams)).thenReturn(executedRequestActions);

        ExecutedRequestActionsMiReportResult report =
            (ExecutedRequestActionsMiReportResult) generator.generateMiReport(entityManager, reportParams);

        assertNotNull(report);
        assertEquals(MiReportType.COMPLETED_WORK, report.getReportType());
        assertThat(report.getResults()).containsExactlyElementsOf(executedRequestActions);
    }

    @Test
    void getReportType() {
        assertThat(generator.getReportType()).isEqualTo(MiReportType.COMPLETED_WORK);
    }
}