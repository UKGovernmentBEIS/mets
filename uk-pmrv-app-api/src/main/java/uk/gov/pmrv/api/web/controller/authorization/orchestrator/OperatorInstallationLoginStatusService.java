package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

@Service
public class OperatorInstallationLoginStatusService extends OperatorAccountTypeLoginStatusAbstractService {

    public OperatorInstallationLoginStatusService(AccountQueryService accountQueryService) {
        super(accountQueryService);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.INSTALLATION;
    }
}
