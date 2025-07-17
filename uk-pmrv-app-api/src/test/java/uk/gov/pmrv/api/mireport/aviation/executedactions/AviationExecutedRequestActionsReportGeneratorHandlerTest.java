package uk.gov.pmrv.api.mireport.aviation.executedactions;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.mireport.MiReportType;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestAction;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsMiReportParams;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsMiReportResult;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationExecutedRequestActionsReportGeneratorHandlerTest {

    @InjectMocks
    private AviationExecutedRequestActionsReportGeneratorHandler generator;

    @Mock
    private AviationExecutedRequestActionsRepository repository;

    @Mock
    private EntityManager entityManager;

    @Test
    void generateMiReport() {
        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
                .reportType(MiReportType.COMPLETED_WORK)
                .fromDate(LocalDate.now())
                .build();
        List<AviationExecutedRequestAction> executedRequestActions = List.of(
                AviationExecutedRequestAction.builder()
                        .accountId("emitterId")
                        .accountName("accountName")
                        .accountStatus(InstallationAccountStatus.LIVE.name())
                        .accountType(AccountType.AVIATION)
                        .legalEntityName("legalEntityName")
                        .requestId("REQ-1")
                        .requestType(RequestType.EMP_ISSUANCE_UKETS.name())
                        .requestStatus(RequestStatus.IN_PROGRESS.name())
                        .requestActionType(RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED.name())
                        .requestActionSubmitter("submitter")
                        .requestActionCompletionDate(LocalDateTime.now())
                        .crcoCode("crcoCode")
                        .build());

        when(repository.findExecutedRequestActions(entityManager, reportParams)).thenReturn(executedRequestActions);

        ExecutedRequestActionsMiReportResult<AviationExecutedRequestAction> report =
                (ExecutedRequestActionsMiReportResult<AviationExecutedRequestAction>)generator.generateMiReport(entityManager, reportParams);

        assertNotNull(report);
        assertEquals(MiReportType.COMPLETED_WORK, report.getReportType());
        assertThat(report.getResults()).containsExactlyElementsOf(executedRequestActions);
    }

    @Test
    void getReportType() {
        assertThat(generator.getReportType()).isEqualTo(MiReportType.COMPLETED_WORK);
    }

    @Test
    void getColumnNames() {
        assertThat(generator.getColumnNames()).containsExactlyElementsOf(AviationExecutedRequestAction.getColumnNames());
    }
}
