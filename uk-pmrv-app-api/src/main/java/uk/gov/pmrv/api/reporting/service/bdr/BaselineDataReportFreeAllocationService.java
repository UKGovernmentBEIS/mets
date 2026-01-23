package uk.gov.pmrv.api.reporting.service.bdr;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.bdr.BaselineDataReportFreeAllocation;
import uk.gov.pmrv.api.reporting.repository.BaselineDataReportFreeAllocationRepository;

@Service
@RequiredArgsConstructor
public class BaselineDataReportFreeAllocationService {

    private final BaselineDataReportFreeAllocationRepository baselineDataReportFreeAllocationRepository;

    public void createFreeAllocationEntry(Long accountId, Boolean freeAllocation) {

        BaselineDataReportFreeAllocation entry = baselineDataReportFreeAllocationRepository
                .findByAccountId(accountId)
                .orElseGet(() -> BaselineDataReportFreeAllocation.builder()
                        .accountId(accountId)
                        .build());

        entry.setFreeAllocation(freeAllocation);
        baselineDataReportFreeAllocationRepository.save(entry);
    }

}
