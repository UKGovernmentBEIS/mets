package uk.gov.pmrv.api.account.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.AuthorityConstants;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PrimaryContactValidator implements AccountContactTypeUpdateValidator, AccountContactTypeDeleteValidator {

    private final AuthorityService authorityService;

    @Override
    public void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId) {
        String userId = contactTypes.get(AccountContactType.PRIMARY);

        if(userId == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_IS_REQUIRED);
        }

        Optional<AuthorityInfoDTO> userAccountAuthorityOptional =
            authorityService.findAuthorityByUserIdAndAccountId(userId, accountId);

        userAccountAuthorityOptional.ifPresent(userAccountAuthority -> {
                if(AuthorityConstants.EMITTER_CONTACT.equals(userAccountAuthority.getCode())) {
                    throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_OPERATOR);
                }
            }
        );
    }

    @Override
    public void validateDelete(Map<AccountContactType, String> contactTypes) {
        String userId = contactTypes.get(AccountContactType.PRIMARY);
        if(userId == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_IS_REQUIRED);
        }
    }
}
