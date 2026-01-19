package uk.gov.pmrv.api.account.aviation.service.reportingstatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusHistoryRepository;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusRepository;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusPopulationServiceTest {

    @InjectMocks
    private AviationAccountReportingStatusPopulationService service;

    @Mock
    private AviationAccountRepository aviationAccountRepository;

    @Mock
    private AviationAccountReportingStatusRepository reportingStatusRepository;

    @Mock
    private AviationAccountReportingStatusHistoryRepository historyRepository;

    @Test
    void execute_should_create_status_and_history_for_eligible_accounts_only() {

        final Year currentYear = Year.now();
        final Long existingAccountId = 1L;
        final Long newAccountId = 2L;


        when(aviationAccountRepository.findIdsByStatusIn(List.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE)))
            .thenReturn(List.of(existingAccountId, newAccountId));

        when(reportingStatusRepository.existsByAccountIdAndYear(existingAccountId, currentYear)).thenReturn(true);
        when(reportingStatusRepository.existsByAccountIdAndYear(newAccountId, currentYear)).thenReturn(false);

        AviationAccount accountProxy = mock(AviationAccount.class);
        when(aviationAccountRepository.getReferenceById(newAccountId)).thenReturn(accountProxy);

        service.populateReportingStatusesForNewYear();

        verify(reportingStatusRepository).existsByAccountIdAndYear(existingAccountId, currentYear);
        verify(reportingStatusRepository).existsByAccountIdAndYear(newAccountId, currentYear);

        verify(aviationAccountRepository, never()).getReferenceById(existingAccountId);
        verify(aviationAccountRepository).getReferenceById(newAccountId);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AviationAccountReportingStatus>> statusCaptor = ArgumentCaptor.forClass(List.class);
        verify(reportingStatusRepository).saveAll(statusCaptor.capture());

        List<AviationAccountReportingStatus> savedStatuses = statusCaptor.getValue();
        assertThat(savedStatuses).hasSize(1);
        AviationAccountReportingStatus savedStatus = savedStatuses.get(0);
        assertThat(savedStatus.getAccount()).isEqualTo(accountProxy);
        assertThat(savedStatus.getYear()).isEqualTo(currentYear);
        assertThat(savedStatus.getStatus()).isEqualTo(AviationAccountReportingStatusType.REQUIRED_TO_REPORT);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AviationAccountReportingStatusHistory>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(historyRepository).saveAll(historyCaptor.capture());

        List<AviationAccountReportingStatusHistory> savedHistories = historyCaptor.getValue();
        assertThat(savedHistories).hasSize(1);
        AviationAccountReportingStatusHistory savedHistory = savedHistories.get(0);
        assertThat(savedHistory.getAccount()).isEqualTo(accountProxy);
        assertThat(savedHistory.getStatus()).isEqualTo(AviationAccountReportingStatusType.REQUIRED_TO_REPORT);
        assertThat(savedHistory.getSubmitterName()).isEqualTo("system");
        assertThat(savedHistory.getSubmissionDate()).isNotNull();
    }

    @Test
    void execute_should_do_nothing_when_no_accounts_found() {
        when(aviationAccountRepository.findIdsByStatusIn(any())).thenReturn(Collections.emptyList());

        service.populateReportingStatusesForNewYear();

        verify(reportingStatusRepository, never()).existsByAccountIdAndYear(any(), any());
        verify(aviationAccountRepository, never()).getReferenceById(any());
        verify(reportingStatusRepository).saveAll(Collections.emptyList()); // or never(), depending on impl, but saveAll([]) is safe
        verify(historyRepository).saveAll(Collections.emptyList());
    }

    @Test
    void execute_should_do_nothing_when_all_accounts_already_have_status() {
        final Year currentYear = Year.now();
        Long accountId = 1L;

        when(aviationAccountRepository.findIdsByStatusIn(any())).thenReturn(List.of(accountId));
        when(reportingStatusRepository.existsByAccountIdAndYear(accountId, currentYear)).thenReturn(true);

        service.populateReportingStatusesForNewYear();

        verify(aviationAccountRepository, never()).getReferenceById(any());

        verify(reportingStatusRepository).saveAll(eq(Collections.emptyList()));
        verify(historyRepository).saveAll(eq(Collections.emptyList()));
    }

}