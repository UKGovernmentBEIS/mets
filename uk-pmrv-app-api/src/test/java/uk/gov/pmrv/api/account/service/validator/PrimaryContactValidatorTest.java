package uk.gov.pmrv.api.account.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.AuthorityConstants.EMITTER_CONTACT;
import static uk.gov.netz.api.authorization.AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;

@ExtendWith(MockitoExtension.class)
class PrimaryContactValidatorTest {

    @InjectMocks
    private PrimaryContactValidator validator;

    @Mock
    private AuthorityService authorityService;
    
    @Test
    void validateDelete_no_exception() {
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "user"
                );
        
        validator.validateDelete(contactTypes);
    }
    
    @Test
    void validateDelete_primary_type_not_exist() {
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.FINANCIAL, "user"
                );
        
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            validator.validateDelete(contactTypes);
        });
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_IS_REQUIRED);
    }

    @Test
    void validateUpdate_primary_contact_not_defined() {
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
            AccountContactType.SECONDARY, "user"
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validateUpdate(contactTypes, accountId));

        assertEquals(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_IS_REQUIRED, ex.getErrorCode());

        verifyNoInteractions(authorityService);
    }

    @Test
    void validateUpdate_primary_contact_is_not_emitter_contact() {
        String userId = "userId";
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
            AccountContactType.PRIMARY, userId
        );
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .userId(userId)
            .code(OPERATOR_ADMIN_ROLE_CODE)
            .build();

        when(authorityService.findAuthorityByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(authorityInfo));

        validator.validateUpdate(contactTypes, accountId);

        verify(authorityService, times(1)).findAuthorityByUserIdAndAccountId(userId, accountId);
    }

    @Test
    void validateUpdate_primary_contact_is_emitter_contact() {
        String userId = "userId";
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
            AccountContactType.PRIMARY, userId
        );
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .userId(userId)
            .code(EMITTER_CONTACT)
            .build();

        when(authorityService.findAuthorityByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(authorityInfo));

        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validateUpdate(contactTypes, accountId));

        assertEquals(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_OPERATOR, ex.getErrorCode());

        verify(authorityService, times(1)).findAuthorityByUserIdAndAccountId(userId, accountId);
    }
    
}
