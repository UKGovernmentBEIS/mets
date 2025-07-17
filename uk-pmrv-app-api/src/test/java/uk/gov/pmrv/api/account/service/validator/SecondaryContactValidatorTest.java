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

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.AuthorityConstants.EMITTER_CONTACT;
import static uk.gov.netz.api.authorization.AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;

@ExtendWith(MockitoExtension.class)
class SecondaryContactValidatorTest {

    @InjectMocks
    private SecondaryContactValidator secondaryContactValidator;

    @Mock
    private AuthorityService authorityService;

    @Test
    void validateUpdate_secondary_contact_is_not_emitter_contact() {
        String userId = "userId";
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
            AccountContactType.SECONDARY, userId
        );
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .userId(userId)
            .code(OPERATOR_ADMIN_ROLE_CODE)
            .build();

        when(authorityService.findAuthorityByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(authorityInfo));

        secondaryContactValidator.validateUpdate(contactTypes, accountId);

        verify(authorityService, times(1)).findAuthorityByUserIdAndAccountId(userId, accountId);
    }

    @Test
    void validateUpdate_secondary_contact_is_emitter_contact_exception() {
        String userId = "userId";
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
            AccountContactType.SECONDARY, userId
        );
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .userId(userId)
            .code(EMITTER_CONTACT)
            .build();

        when(authorityService.findAuthorityByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(authorityInfo));

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> secondaryContactValidator.validateUpdate(contactTypes, accountId));

        assertEquals(ErrorCode.ACCOUNT_CONTACT_TYPE_SECONDARY_CONTACT_NOT_OPERATOR, businessException.getErrorCode());
        verify(authorityService, times(1)).findAuthorityByUserIdAndAccountId(userId, accountId);
    }

    @Test
    void validateUpdate_secondary_contact_not_defined() {
        String userId = "userId";
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
            AccountContactType.PRIMARY, userId
        );

        secondaryContactValidator.validateUpdate(contactTypes, accountId);
        verifyNoInteractions(authorityService);
    }
}