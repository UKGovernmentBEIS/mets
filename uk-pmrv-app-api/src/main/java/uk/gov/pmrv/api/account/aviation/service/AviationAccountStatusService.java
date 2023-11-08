package uk.gov.pmrv.api.account.aviation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;

@Service
@RequiredArgsConstructor
public class AviationAccountStatusService {

    private final AviationAccountQueryService aviationAccountQueryService;

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'NEW'}")
    public void handleEmpApproved(final Long accountId) {
        AviationAccount account = aviationAccountQueryService.getAccountById(accountId);

        account.setStatus(AviationAccountStatus.LIVE);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'CLOSED'}")
	public void handleCloseAccount(Long accountId) {
    	AviationAccount account = aviationAccountQueryService.getAccountById(accountId);
        account.setStatus(AviationAccountStatus.CLOSED);
	}
}
