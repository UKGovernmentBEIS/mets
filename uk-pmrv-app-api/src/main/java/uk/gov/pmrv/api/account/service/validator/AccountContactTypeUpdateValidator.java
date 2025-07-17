package uk.gov.pmrv.api.account.service.validator;

import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

import java.util.Map;

public interface AccountContactTypeUpdateValidator {
    
    void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId);
}
