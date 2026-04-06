package uk.gov.pmrv.api.web.orchestrator.account.aviation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusListResponse;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusRepository;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountReportingStatusMapper;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AviationAccountReportingStatusQueryOrchestrator {

    private final AviationAccountReportingStatusRepository aviationAccountReportingStatusRepository;

    private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    private final AviationAccountRepository aviationAccountRepository;

    private final AviationAccountReportingStatusMapper mapper;

    @Transactional(readOnly = true)
    public AviationAccountReportingStatusListResponse getAviationAccountReportingStatuses(Long accountId, Integer page , Integer pageSize) {
        Page<AviationAccountReportingStatus> reportingStatusList =
                aviationAccountReportingStatusRepository.findByAccountIdOrderByYearDesc(PageRequest.of(page, pageSize), accountId);

        return AviationAccountReportingStatusListResponse.builder()
                .reportingStatusList(reportingStatusList.get()
                    .map(status -> mapper.toReportingStatusDTO(status, aviationReportableEmissionsRepository.existsByAccountIdAndYear(accountId, status.getYear())))
                .toList())
                .total(reportingStatusList.getTotalElements())
                .build();

    }

    @Transactional
    public List<AviationAccountReportingStatus> getAllReportingStatusesForAccount(Long accountId) {
        return aviationAccountReportingStatusRepository.findByAccountIdOrderByYearDesc(accountId);
    }

    @Transactional
    public void addReportingStatusesForYears(List<Integer> years, Long accountId) {
        List<AviationAccountReportingStatus> aviationAccountReportingStatuses = new ArrayList<>();
        years.forEach(year -> {
            if(!aviationAccountReportingStatusRepository.existsByAccountIdAndYear(accountId,Year.of(year))) {
                aviationAccountReportingStatuses.add(AviationAccountReportingStatus.builder()
                        .status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT)
                        .year(Year.of(year)).account(aviationAccountRepository.getReferenceById(accountId))
                        .build());
            }

        });
        if(!aviationAccountReportingStatuses.isEmpty()) {
            aviationAccountReportingStatusRepository.saveAll(aviationAccountReportingStatuses);
        }
    }

}
