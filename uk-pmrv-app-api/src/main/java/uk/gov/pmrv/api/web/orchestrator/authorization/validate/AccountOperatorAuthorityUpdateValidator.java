package uk.gov.pmrv.api.web.orchestrator.authorization.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.AccountOperatorAuthorityUpdateWrapperDTO;

public class AccountOperatorAuthorityUpdateValidator 
            implements ConstraintValidator<AccountOperatorAuthorityUpdate, AccountOperatorAuthorityUpdateWrapperDTO>{

    @Override
    public boolean isValid(AccountOperatorAuthorityUpdateWrapperDTO dto, ConstraintValidatorContext context) {
        return ! (
                ObjectUtils.isEmpty(dto.getAccountOperatorAuthorityUpdateList()) &&
                ObjectUtils.isEmpty(dto.getContactTypes())
                );
    }

}
