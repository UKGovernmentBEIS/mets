package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;

import java.util.List;

public interface OperatorAccountTypeLoginStatusService {

    LoginStatus getLoginStatus(List<Authority> userAuthorities);

    AccountType getAccountType();
}
