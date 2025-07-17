package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.service.UserSecuritySetupService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserManagementServiceTest {

	@InjectMocks
        private OperatorUserManagementService service;
	
	@Mock
	private OperatorUserAuthService operatorUserAuthService;
	
	@Mock
	private OperatorAuthorityQueryService operatorAuthorityQueryService;
	
	@Mock
	private UserSecuritySetupService userSecuritySetupService;
	
	@Test
	void updateOperatorUser() {
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		service.updateOperatorUser(operatorUserDTO);

		verify(operatorUserAuthService, times(1)).updateUser(operatorUserDTO);
	}
	

	@Test
	void getOperatorUserByAccountAndIdTestDifferentUserWithPermission() {
		final Long accountId = 1L;
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId2).authorityStatus(AuthorityStatus.ACTIVE).build());

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		service.getOperatorUserByAccountAndId(accountId, userId2);

		// Verify
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, times(1)).getOperatorUserById(userId2);
	}

	@Test
	void getOperatorUserByAccountAndIdTestUserNotExists() {
		final Long accountId = 1L;
		final String userId1 = "userId1";
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId1).authorityStatus(AuthorityStatus.ACTIVE).build());

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.getOperatorUserByAccountAndId(accountId, userId2));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
	}

	@Test
	void getOperatorUserByAccountAndIdTestAccountNotExists() {
		final Long accountId = 1L;
		final String userId = "userId";

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(new ArrayList<>());

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.getOperatorUserByAccountAndId(accountId, userId));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
	}

	@Test
	void updateOperatorUserByAccountAndIdTestDifferentUserWithPermission() {
		final Long accountId = 1L;
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId2).authorityStatus(AuthorityStatus.ACTIVE).build());
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		service.updateOperatorUserByAccountAndId(accountId, userId2, operatorUserDTO);

		// Verify
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, times(1)).updateUser(operatorUserDTO);
	}

	@Test
	void updateOperatorUserByAccountAndIdTestUserNotExists() {
		final Long accountId = 1L;
		final String userId1 = "userId1";
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId1).authorityStatus(AuthorityStatus.ACTIVE).build());
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.updateOperatorUserByAccountAndId(accountId, userId2, operatorUserDTO));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, never()).updateUser(Mockito.any(OperatorUserDTO.class));
	}

	@Test
	void updateOperatorUserByAccountAndIdTestAccountNotExists() {
		final Long accountId = 1L;
		final String userId2 = "userId2";
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(new ArrayList<>());

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.updateOperatorUserByAccountAndId(accountId, userId2, operatorUserDTO));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, never()).updateUser(Mockito.any(OperatorUserDTO.class));
	}
	
	@Test
	void resetOperator2Fa() {
		final Long accountId = 1L;
		final String userId = "userId";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId).authorityStatus(AuthorityStatus.ACTIVE).build());

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		service.resetOperator2Fa(accountId, userId);

		// Verify
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(userSecuritySetupService, times(1)).resetUser2Fa(userId);
	}
	
	@Test
	void resetOperator2Fa_user_not_related_to_account() {
		final Long accountId = 1L;
		final String userId1 = "userId1";
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId1).authorityStatus(AuthorityStatus.ACTIVE).build());

		when(operatorAuthorityQueryService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.resetOperator2Fa(accountId, userId2));

		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(userSecuritySetupService, never()).resetUser2Fa(anyString());
	}
	
	private OperatorUserDTO buildOperatorUserDTO() {
		return OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();
	}
}
