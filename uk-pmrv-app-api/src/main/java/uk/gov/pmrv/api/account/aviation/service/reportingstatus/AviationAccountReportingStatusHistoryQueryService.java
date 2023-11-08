package uk.gov.pmrv.api.account.aviation.service.reportingstatus;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryListResponse;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusHistoryRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountReportingStatusHistoryMapper;

@Service
@RequiredArgsConstructor
public class AviationAccountReportingStatusHistoryQueryService {

    private final AviationAccountReportingStatusHistoryRepository repository;

    private final AviationAccountReportingStatusHistoryMapper mapper;

    public AviationAccountReportingStatusHistoryListResponse getReportingStatusHistoryListResponse(Long accountId, Integer page, Integer pageSize) {
        Page<AviationAccountReportingStatusHistory> reportingStatusHistoryList =
            repository.findByAccountIdOrderBySubmissionDateDesc(PageRequest.of(page, pageSize), accountId);

        return AviationAccountReportingStatusHistoryListResponse.builder()
            .reportingStatusHistoryList(reportingStatusHistoryList.get().map(mapper::toReportingStatusHistoryDTO).toList())
            .total(reportingStatusHistoryList.getTotalElements())
            .build();
    }
}
