package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;

@Service
@RequiredArgsConstructor
public class NonComplianceSendOfficialNoticeServiceDelegator {

	private final List<NonComplianceSendOfficialNoticeService> nonComplianceSendOfficialNoticeServices;
	
	public void sendOfficialNotice(final UUID officialNotice,
            final Request request,
            final NonComplianceDecisionNotification decisionNotification) {
		
		NonComplianceSendOfficialNoticeService nonComplianceSendOfficialNoticeService = 
				getNonComplianceSendOfficialNotice(request.getType().getAccountType())
				.orElseThrow(() -> new BusinessException(MetsErrorCode.INVALID_ACCOUNT_TYPE));
		nonComplianceSendOfficialNoticeService.sendOfficialNotice(officialNotice, request, decisionNotification);
	}
	
	private Optional<NonComplianceSendOfficialNoticeService> getNonComplianceSendOfficialNotice(AccountType accountType) {
        return nonComplianceSendOfficialNoticeServices.stream()
            .filter(service -> service.getAccountType().equals(accountType))
            .findFirst();
    }
}
