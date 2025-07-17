package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserRegisteredAcceptInvitationServiceTest {

	@InjectMocks
    private OperatorUserRegisteredAcceptInvitationService cut;
	
	@Mock
	private OperatorAuthorityService operatorAuthorityService;
	
	@Mock
	private UserRoleTypeService userRoleTypeService;
	
	@Mock
	private UserAuthService userAuthService;
	
	@Mock
	private OperatorUserNotificationGateway operatorUserNotificationGateway;
	
	@Test
	void acceptAuthorityAndNotify() {
		Long authorityId = 1L;
		
		Authority authority = Authority.builder().createdBy("createdBy")
				.userId("invitee")
				.build();
		
		UserInfoDTO inviteeUser = UserInfoDTO.builder().userId("invitee").build();
		UserInfoDTO inviterUser = UserInfoDTO.builder().userId("createdBy").build();
		
		when(operatorAuthorityService.acceptAuthority(authorityId)).thenReturn(authority);
		when(userAuthService.getUserByUserId("invitee")).thenReturn(inviteeUser);
		when(userAuthService.getUserByUserId("createdBy")).thenReturn(inviterUser);
		
		cut.acceptAuthorityAndNotify(authorityId);
		
		verify(operatorAuthorityService, times(1)).acceptAuthority(authorityId);
		verify(userRoleTypeService, times(1)).createUserRoleTypeIfNotExist(authority.getUserId(), RoleTypeConstants.OPERATOR);
		verify(userAuthService, times(1)).getUserByUserId("invitee");
		verify(userAuthService, times(1)).getUserByUserId("createdBy");
		verify(operatorUserNotificationGateway, times(1)).notifyInviteeAcceptedInvitation(inviteeUser);
		verify(operatorUserNotificationGateway, times(1)).notifyInviterAcceptedInvitation(inviteeUser, inviterUser);
	}
	
}
