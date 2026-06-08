package uk.gov.pmrv.api.account.aviation.service.reportingstatus;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusHistoryRepository;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusRepository;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAccountReportingStatusService {

	private final AviationAccountRepository aviationAccountRepository;
	private final AviationAccountReportingStatusRepository reportingStatusRepository;
	private final AviationAccountReportingStatusHistoryRepository historyRepository;

	public void populateReportingStatusesForNewYear() {
		final Year currentYear = Year.now();
		final LocalDateTime executionTime = LocalDateTime.now();

		List<Long> accountIds = aviationAccountRepository.findIdsByStatusIn(
			List.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE)
		);

		List<AviationAccountReportingStatus> newStatuses = new ArrayList<>();
		List<AviationAccountReportingStatusHistory> newHistories = new ArrayList<>();

		for (Long accountId : accountIds) {

			if (reportingStatusRepository.existsByAccountIdAndYear(accountId, currentYear)) {
				continue;
			}

			AviationAccount accountProxy = aviationAccountRepository.getReferenceById(accountId);

			newStatuses.add(createStatus(accountProxy, currentYear));
			newHistories.add(createHistory(accountProxy, currentYear, executionTime));
		}

		reportingStatusRepository.saveAll(newStatuses);
		historyRepository.saveAll(newHistories);
	}

	public Optional<AviationAccountReportingStatus> getReportingStatusByYear(Long accountId, Year year) {
		return reportingStatusRepository.findByAccountIdAndYear(accountId, year);
	}

	private AviationAccountReportingStatus createStatus(AviationAccount accountProxy, Year year) {
		return AviationAccountReportingStatus.builder()
			.account(accountProxy)
			.year(year)
			.status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT)
			.build();
	}

	private AviationAccountReportingStatusHistory createHistory(AviationAccount accountProxy, Year year, LocalDateTime now) {
		return AviationAccountReportingStatusHistory.builder()
			.account(accountProxy)
			.year(year)
			.status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT)
			.submissionDate(now)
			.submitterName("system")
			.submitterId("system")
			.build();
	}

}
