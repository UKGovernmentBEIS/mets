package uk.gov.pmrv.api.account.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.AuthorityConstants;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecondaryContactValidator implements AccountContactTypeUpdateValidator {

    private final AuthorityService authorityService;

    @Override
    public void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId) {
        String userId = contactTypes.get(AccountContactType.SECONDARY);
        if (userId != null) {
            Optional<AuthorityInfoDTO> userAccountAuthorityOptional =
                authorityService.findAuthorityByUserIdAndAccountId(userId, accountId);

            userAccountAuthorityOptional.ifPresent(userAccountAuthority -> {
                if(AuthorityConstants.EMITTER_CONTACT.equals(userAccountAuthority.getCode())) {
                    throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SECONDARY_CONTACT_NOT_OPERATOR);
                }
            });
        }
    }
}
