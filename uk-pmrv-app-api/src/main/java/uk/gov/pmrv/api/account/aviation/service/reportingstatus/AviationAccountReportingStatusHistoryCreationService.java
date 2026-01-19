package uk.gov.pmrv.api.account.aviation.service.reportingstatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingExemptEvent;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingRequiredEvent;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptFlagEvent;

import java.time.LocalDateTime;
import java.time.Year;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Validated
@Service
@RequiredArgsConstructor
public class AviationAccountReportingStatusHistoryCreationService {

	private final AviationAccountRepository aviationAccountRepository;
    private final ApplicationEventPublisher publisher;

	@Transactional
	@AccountStatus(expression = "{#status != 'CLOSED'}")
	public void submitReportingStatus(Long accountId,
			@Valid AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO,
									  AppUser appUser) {

		AviationAccount account = aviationAccountRepository.findAviationAccountById(accountId).orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

		AviationAccountReportingStatus accountReportingStatus = account.getReportingStatusByYear(reportingStatusHistoryCreationDTO.getYear())
			.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

		final AviationAccountReportingStatusType newReportingStatus = reportingStatusHistoryCreationDTO.getStatus();

		if (!accountReportingStatus.getStatus().equals(newReportingStatus)) {

			final AviationAccountReportingStatusType currentReportingStatus = accountReportingStatus.getStatus();
			LocalDateTime currentDateTime = LocalDateTime.now();

			account.addReportingStatusHistory(AviationAccountReportingStatusHistory.builder()
					.status(reportingStatusHistoryCreationDTO.getStatus())
					.submissionDate(currentDateTime)
					.reason(reportingStatusHistoryCreationDTO.getReason())
					.submitterId(appUser.getUserId())
					.submitterName(appUser.getFullName())
					.year(reportingStatusHistoryCreationDTO.getYear())
					.build());
			accountReportingStatus.setStatus(reportingStatusHistoryCreationDTO.getStatus());
			accountReportingStatus.setReason(reportingStatusHistoryCreationDTO.getReason());

			if(Year.now().getValue() > reportingStatusHistoryCreationDTO.getYear().getValue()) {
				publishReportingStatusChangedEvent(account, currentReportingStatus, newReportingStatus,
						appUser.getUserId(),reportingStatusHistoryCreationDTO.getYear());
			}
			publisher.publishEvent(AviationAccountExemptFlagEvent.builder().accountId(account.getId())
					.registryId(account.getRegistryId())
					.isExempt(!reportingStatusHistoryCreationDTO.getStatus()
                            .equals(AviationAccountReportingStatusType.REQUIRED_TO_REPORT))
					.year(reportingStatusHistoryCreationDTO.getYear()).build());
		} else {
			throw new BusinessException(MetsErrorCode.AVIATION_ACCOUNT_REPORTING_STATUS_NOT_CHANGED, accountId,
					reportingStatusHistoryCreationDTO.getStatus());
		}
	}

	private void publishReportingStatusChangedEvent(AviationAccount account,
                                                    AviationAccountReportingStatusType currentReportingStatus, AviationAccountReportingStatusType newReportingStatus,
                                                    String submitterId,Year year) {
        //if change from REQUIRED_TO_REPORT to any other ReportingStatus (EXEMPT_COMMERCIAL or EXEMPT_NON_COMMERCIAL)
		if (AviationAccountReportingStatusType.REQUIRED_TO_REPORT == currentReportingStatus) {
			publisher.publishEvent(AviationAccountReportingExemptEvent.builder().accountId(account.getId())
					.year(year).submitterId(submitterId).build());
		}

		if (AviationAccountReportingStatusType.REQUIRED_TO_REPORT == newReportingStatus) {
			publisher.publishEvent(AviationAccountReportingRequiredEvent.builder().accountId(account.getId())
					.year(year).submitterId(submitterId).build());
		}
    }

}
