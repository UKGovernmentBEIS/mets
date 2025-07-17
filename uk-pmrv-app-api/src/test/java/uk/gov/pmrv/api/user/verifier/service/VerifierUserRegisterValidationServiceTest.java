package uk.gov.pmrv.api.user.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
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
class VerifierUserRegisterValidationServiceTest {

	@InjectMocks
    private VerifierUserRegisterValidationService cut;

    @Mock
    private VerifierAuthorityService verifierAuthorityService;
    
    @Mock
    private UserRoleTypeService userRoleTypeService;
    
    @Test
    void validate() {
    	String userId = "userId";
    	Long vbId = 1L;
    	
		when(verifierAuthorityService.existsNonPendingAuthorityForVB(userId, vbId)).thenReturn(false);
		when(verifierAuthorityService.existsAuthorityNotForVB(userId, vbId)).thenReturn(false);
		
		cut.validate(userId, vbId);
		
		verify(verifierAuthorityService, times(1)).existsNonPendingAuthorityForVB(userId, vbId);
		verify(verifierAuthorityService, times(1)).existsAuthorityNotForVB(userId, vbId);
		verify(userRoleTypeService, times(1)).validateUserRoleTypeIsEmpty(userId);
    }
    
    @Test
    void validate_update_non_pending() {
    	String userId = "userId";
    	Long vbId = 1L;
    	
		when(verifierAuthorityService.existsNonPendingAuthorityForVB(userId, vbId)).thenReturn(true);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.validate(userId, vbId));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
		
		verify(verifierAuthorityService, times(1)).existsNonPendingAuthorityForVB(userId, vbId);
		verifyNoMoreInteractions(verifierAuthorityService);
		verifyNoInteractions(userRoleTypeService);
    }
    
    @Test
    void validate_different_role() {
    	String userId = "userId";
    	Long vbId = 1L;
    	
		when(verifierAuthorityService.existsNonPendingAuthorityForVB(userId, vbId)).thenReturn(false);
		when(verifierAuthorityService.existsAuthorityNotForVB(userId, vbId)).thenReturn(true);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.validate(userId, vbId));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_OR_VB);
		
		verify(verifierAuthorityService, times(1)).existsNonPendingAuthorityForVB(userId, vbId);
		verify(verifierAuthorityService, times(1)).existsAuthorityNotForVB(userId, vbId);
		verifyNoInteractions(userRoleTypeService);
    }
}
