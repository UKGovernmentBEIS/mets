package uk.gov.pmrv.api.account.aviation.service.reportingstatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryListResponse;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusHistoryRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountReportingStatusHistoryMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusHistoryQueryServiceTest {

    @InjectMocks
    private AviationAccountReportingStatusHistoryQueryService service;

    @Mock
    private AviationAccountReportingStatusHistoryRepository repository;

    @Mock
    private AviationAccountReportingStatusHistoryMapper mapper;

    @Test
    void getReportingStatusHistoryListResponse() {
        Long accountId = 1L;
        AviationAccountReportingStatus status = AviationAccountReportingStatus.REQUIRED_TO_REPORT;
        String reason = "reason";
        String submitterName = "submitterName";
        LocalDateTime submissionDate = LocalDateTime.now();
        AviationAccountReportingStatusHistory reportingStatusEntry = AviationAccountReportingStatusHistory.builder()
                .id(1L)
                .status(status)
                .reason(reason)
                .submitterName(submitterName)
                .submissionDate(submissionDate)
                .account(AviationAccount.builder().id(accountId).build())
                .build();
        List<AviationAccountReportingStatusHistory> reportingStatusEntries = List.of(reportingStatusEntry);
        Page<AviationAccountReportingStatusHistory> pagedResponse = new PageImpl<>(reportingStatusEntries);
        when(repository
                .findByAccountIdOrderBySubmissionDateDesc(PageRequest.of(0, 1), accountId))
                .thenReturn(pagedResponse);
        when(mapper.toReportingStatusHistoryDTO(reportingStatusEntry)).thenReturn(AviationAccountReportingStatusHistoryDTO.builder()
                        .status(status)
                        .reason(reason)
                        .submitterName(submitterName)
                        .submissionDate(submissionDate)
                .build());


        final AviationAccountReportingStatusHistoryListResponse expectedHistoryResponse = AviationAccountReportingStatusHistoryListResponse.builder()
            .reportingStatusHistoryList(List.of(AviationAccountReportingStatusHistoryDTO.builder()
                .status(status)
                .reason(reason)
                .submitterName(submitterName)
                .submissionDate(submissionDate)
                .build()))
            .total(1L)
            .build();

        final AviationAccountReportingStatusHistoryListResponse actualHistoryResponse = service.getReportingStatusHistoryListResponse(accountId, 0, 1);

        assertEquals(expectedHistoryResponse, actualHistoryResponse);
    }

    @Test
    void getReportingStatusHistory_no_results() {
        Long accountId = 1L;

        when(repository
                .findByAccountIdOrderBySubmissionDateDesc(PageRequest.of(0, 1), accountId))
                .thenReturn(Page.empty());

        final AviationAccountReportingStatusHistoryListResponse expectedHistoryResponse = AviationAccountReportingStatusHistoryListResponse.builder()
            .reportingStatusHistoryList(List.of())
            .total(0L)
            .build();

        final AviationAccountReportingStatusHistoryListResponse actualHistoryResponse = service.getReportingStatusHistoryListResponse(accountId, 0, 1);

        assertEquals(expectedHistoryResponse, actualHistoryResponse);
    }
}
