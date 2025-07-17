package uk.gov.pmrv.api.account.service.validator;

import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

import java.util.Map;

public interface AccountContactTypeDeleteValidator {

    void validateDelete(Map<AccountContactType, String> contactTypes);
}
