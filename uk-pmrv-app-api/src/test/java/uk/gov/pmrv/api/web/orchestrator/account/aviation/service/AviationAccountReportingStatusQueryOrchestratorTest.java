package uk.gov.pmrv.api.web.orchestrator.account.aviation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusListResponse;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusRepository;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountReportingStatusMapper;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusQueryOrchestratorTest {

    @Mock
    private AviationAccountReportingStatusRepository aviationAccountReportingStatusRepository;

    @Mock
    private AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    @Mock
    private AviationAccountReportingStatusMapper mapper;

    @Mock
    private AviationAccountRepository aviationAccountRepository;

    @InjectMocks
    private AviationAccountReportingStatusQueryOrchestrator orchestrator;

    @Test
    void getAviationAccountReportingStatuses() {
        Long accountId = 1L;
        int page = 0;
        int pageSize = 10;
        Year year = Year.of(2023);
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        AviationAccountReportingStatus reportingStatus = AviationAccountReportingStatus.builder()
                .year(year)
                .build();

        Page<AviationAccountReportingStatus> pagedResult = new PageImpl<>(List.of(reportingStatus));

        AviationAccountReportingStatusDTO dto = AviationAccountReportingStatusDTO.builder()
                .year(year)
                .isReported(true)
                .build();

        when(aviationAccountReportingStatusRepository.findByAccountIdOrderByYearDesc(pageRequest, accountId))
                .thenReturn(pagedResult);
        when(aviationReportableEmissionsRepository.existsByAccountIdAndYear(accountId, year)).thenReturn(true);
        when(mapper.toReportingStatusDTO(reportingStatus, true)).thenReturn(dto);

        AviationAccountReportingStatusListResponse response =
                orchestrator.getAviationAccountReportingStatuses(accountId, page, pageSize);

        assertNotNull(response);
        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getReportingStatusList().size());

        AviationAccountReportingStatusDTO resultDto = response.getReportingStatusList().get(0);
        assertEquals(year, resultDto.getYear());
        assertTrue(resultDto.getIsReported());

        verify(aviationAccountReportingStatusRepository).findByAccountIdOrderByYearDesc(pageRequest, accountId);
        verify(aviationReportableEmissionsRepository).existsByAccountIdAndYear(accountId, year);
        verify(mapper).toReportingStatusDTO(reportingStatus, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    void addReportingStatusesForYears() {
        Long accountId = 1L;
        List<Integer> years = List.of(2022, 2023);
        AviationAccount aviationAccount = mock(AviationAccount.class);

        when(aviationAccountRepository.getReferenceById(accountId)).thenReturn(aviationAccount);

        orchestrator.addReportingStatusesForYears(years, accountId);

        ArgumentCaptor<List<AviationAccountReportingStatus>> captor = ArgumentCaptor.forClass(List.class);
        verify(aviationAccountReportingStatusRepository).saveAll(captor.capture());

        List<AviationAccountReportingStatus> savedStatuses = captor.getValue();
        assertEquals(2, savedStatuses.size());

        assertEquals(Year.of(2022), savedStatuses.get(0).getYear());
        assertEquals(AviationAccountReportingStatusType.REQUIRED_TO_REPORT, savedStatuses.get(0).getStatus());
        assertEquals(aviationAccount, savedStatuses.get(0).getAccount());

        assertEquals(Year.of(2023), savedStatuses.get(1).getYear());
        assertEquals(AviationAccountReportingStatusType.REQUIRED_TO_REPORT, savedStatuses.get(1).getStatus());
        assertEquals(aviationAccount, savedStatuses.get(1).getAccount());

        verify(aviationAccountRepository, times(2)).getReferenceById(accountId);
    }
}