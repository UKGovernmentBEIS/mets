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
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

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
		final AviationAccount aviationAccount = aviationAccountRepository.findById(accountId)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

		final AviationAccountReportingStatus currentReportingStatus = aviationAccount.getReportingStatus();
		final AviationAccountReportingStatus newReportingStatus = reportingStatusHistoryCreationDTO.getStatus();

		if (newReportingStatus != currentReportingStatus) {
			aviationAccount.setReportingStatus(newReportingStatus);
			aviationAccount.addReportingStatusHistory(AviationAccountReportingStatusHistory.builder()
					.status(reportingStatusHistoryCreationDTO.getStatus())
					.reason(reportingStatusHistoryCreationDTO.getReason())
					.submitterId(appUser.getUserId())
					.submitterName(appUser.getFullName()).build());
			
			publishReportingStatusChangedEvent(accountId, currentReportingStatus, newReportingStatus,
					appUser.getUserId());
		} else {
			throw new BusinessException(MetsErrorCode.AVIATION_ACCOUNT_REPORTING_STATUS_NOT_CHANGED, accountId,
					reportingStatusHistoryCreationDTO.getStatus());
		}
	}

	private void publishReportingStatusChangedEvent(Long accountId,
			AviationAccountReportingStatus currentReportingStatus, AviationAccountReportingStatus newReportingStatus,
			String submitterId) {
        //if change from REQUIRED_TO_REPORT to any other ReportingStatus (EXEMPT_COMMERCIAL or EXEMPT_NON_COMMERCIAL)
		if (AviationAccountReportingStatus.REQUIRED_TO_REPORT == currentReportingStatus) {
			publisher.publishEvent(AviationAccountReportingExemptEvent.builder().accountId(accountId)
					.submitterId(submitterId).build());
		}

		if (AviationAccountReportingStatus.REQUIRED_TO_REPORT == newReportingStatus) {
			publisher.publishEvent(AviationAccountReportingRequiredEvent.builder().accountId(accountId)
					.submitterId(submitterId).build());
		}
    }
}
