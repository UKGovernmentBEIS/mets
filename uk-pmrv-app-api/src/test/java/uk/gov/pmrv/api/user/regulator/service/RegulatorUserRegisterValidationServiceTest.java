package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorUserRegisterValidationServiceTest {

	@InjectMocks
    private RegulatorUserRegisterValidationService cut;

    @Mock
    private RegulatorAuthorityService regulatorAuthorityService;
    
    @Mock
    private UserRoleTypeService userRoleTypeService;
    
    @Test
    void validate() {
    	String userId = "userId";
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	
		when(regulatorAuthorityService.existsNonPendingAuthorityForCA(userId, ca)).thenReturn(false);
		when(regulatorAuthorityService.existsAuthorityNotForCA(userId, ca)).thenReturn(false);
		
		cut.validate(userId, ca);
		
		verify(regulatorAuthorityService, times(1)).existsNonPendingAuthorityForCA(userId, ca);
		verify(regulatorAuthorityService, times(1)).existsAuthorityNotForCA(userId, ca);
		verify(userRoleTypeService, times(1)).validateUserRoleTypeIsEmpty(userId);
    }
    
    @Test
    void validate_update_non_pending() {
    	String userId = "userId";
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	
		when(regulatorAuthorityService.existsNonPendingAuthorityForCA(userId, ca)).thenReturn(true);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.validate(userId, ca));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
		
		verify(regulatorAuthorityService, times(1)).existsNonPendingAuthorityForCA(userId, ca);
		verifyNoMoreInteractions(regulatorAuthorityService);
		verifyNoInteractions(userRoleTypeService);
    }
    
    @Test
    void validate_different_role() {
    	String userId = "userId";
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	
		when(regulatorAuthorityService.existsNonPendingAuthorityForCA(userId, ca)).thenReturn(false);
		when(regulatorAuthorityService.existsAuthorityNotForCA(userId, ca)).thenReturn(true);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.validate(userId, ca));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_OR_CA);
		
		verify(regulatorAuthorityService, times(1)).existsNonPendingAuthorityForCA(userId, ca);
		verify(regulatorAuthorityService, times(1)).existsAuthorityNotForCA(userId, ca);
		verifyNoInteractions(userRoleTypeService);
    }
}
