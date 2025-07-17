package uk.gov.pmrv.api.account.service.validator;

import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

import java.util.Map;

@Component
public class PrimaryAndSecondaryContactNotIdenticalValidator implements AccountContactTypeUpdateValidator {

    @Override
    public void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId) {
        String primaryContact = contactTypes.get(AccountContactType.PRIMARY);
        String secondaryContact = contactTypes.get(AccountContactType.SECONDARY);
        if(primaryContact == null || 
                secondaryContact == null) {
            return;
        }
        
        if(primaryContact.equals(secondaryContact)) {
            throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_AND_SECONDARY_CONTACT_ARE_IDENTICAL);
        }
    }

}
