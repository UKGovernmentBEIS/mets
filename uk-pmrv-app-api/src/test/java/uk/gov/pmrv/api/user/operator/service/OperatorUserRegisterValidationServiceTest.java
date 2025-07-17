package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserRegisterValidationServiceTest {

	@InjectMocks
    private OperatorUserRegisterValidationService cut;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;
    
    @Mock
    private UserRoleTypeService userRoleTypeService;
    
    @Test
    void validateRegisterForAccount() {
    	String userId = "userId";
    	Long accountId = 1L;
    	
    	when(operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)).thenReturn(false);
		when(operatorAuthorityQueryService.existsNonPendingAuthorityForAccount(userId, accountId)).thenReturn(false);
		
		cut.validateRegisterForAccount(userId, accountId);
		
		verify(operatorAuthorityQueryService, times(1)).existsAuthorityNotForAccount(userId);
		verify(userRoleTypeService, times(1)).validateUserRoleTypeIsOfTypeOrNotExist(userId, RoleTypeConstants.OPERATOR);
		verify(operatorAuthorityQueryService, times(1)).existsNonPendingAuthorityForAccount(userId, accountId);
    }
    
    @Test
    void validateRegisterForAccount_different_role() {
    	String userId = "userId";
    	Long accountId = 1L;
    	
		when(operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)).thenReturn(true);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.validateRegisterForAccount(userId, accountId));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_OPERATOR);
		
		verify(operatorAuthorityQueryService, times(1)).existsAuthorityNotForAccount(userId);
		verifyNoInteractions(userRoleTypeService);
		verifyNoMoreInteractions(operatorAuthorityQueryService);
    }
    
    @Test
    void validateRegisterForAccount_update_non_pending() {
    	String userId = "userId";
    	Long accountId = 1L;
    	
    	when(operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)).thenReturn(false);
		when(operatorAuthorityQueryService.existsNonPendingAuthorityForAccount(userId, accountId)).thenReturn(true);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.validateRegisterForAccount(userId, accountId));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
		
		verify(operatorAuthorityQueryService, times(1)).existsAuthorityNotForAccount(userId);
		verify(userRoleTypeService, times(1)).validateUserRoleTypeIsOfTypeOrNotExist(userId, RoleTypeConstants.OPERATOR);
		verify(operatorAuthorityQueryService, times(1)).existsNonPendingAuthorityForAccount(userId, accountId);
		verifyNoMoreInteractions(operatorAuthorityQueryService);
    }
    
}
