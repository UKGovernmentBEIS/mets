package uk.gov.pmrv.api.account.service.validator;

import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

import java.util.Map;

@Component
public class FinancialContactValidator implements AccountContactTypeUpdateValidator, AccountContactTypeDeleteValidator {

    @Override
    public void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId) {
        validateFinancialContactExists(contactTypes);
    }

    @Override
    public void validateDelete(Map<AccountContactType, String> contactTypes) {
        validateFinancialContactExists(contactTypes);
    }

    private void validateFinancialContactExists(Map<AccountContactType, String> contactTypes) {
        String userId = contactTypes.get(AccountContactType.FINANCIAL);
        if(userId == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_FINANCIAL_CONTACT_IS_REQUIRED);
        }
    }
}
