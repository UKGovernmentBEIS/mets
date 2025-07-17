package uk.gov.pmrv.api.user.core.service.auth;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.pmrv.api.user.core.config.KeycloakProperties;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.pmrv.api.user.core.domain.model.core.SignatureRequest;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String REALM = "realm";

    @InjectMocks
    private AuthService authService;

    @Mock
    private Keycloak keycloakAdminClient;

    @Mock
    private KeycloakCustomClient keycloakCustomClient;

    @Mock
    private KeycloakProperties keycloakProperties;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private Response response;

    @Mock
    private UserResource userResource;

    @Spy
    private final Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    @Test
    void getUserRepresentationById() {
        String userId = "user";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);

        mockGetUserResource(userId, userRepresentation);

        UserRepresentation result = authService.getUserRepresentationById(userId);

        assertThat(result).isEqualTo(userRepresentation);

        verifyGetUserResource(userId);
    }

    @Test
    void getByUsername() {
        final String id = "userId";
        final String userName = "userName";
        final String email = "email@email";
        final UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(userName);
        userRepresentation.setEmail(email);

        //mock
        mockFindByEmail(List.of(userRepresentation), email);

        //invoke
        Optional<UserRepresentation> result = authService.getByUsername(email);

        //assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());

        //verify mocks
        verifyFindByEmail(email);
    }

    @Test
    void getByUsername_empty() {
        String email = "email@email";
        //mock
        mockFindByEmail(Collections.emptyList(), email);

        //invoke
        Optional<UserRepresentation> result = authService.getByUsername(email);

        //assert
        assertFalse(result.isPresent());

        //verify mocks
        verifyFindByEmail(email);
    }

    @Test
    void getUsers() {
        List<String> userIds = List.of("user1", "user2");
        List<UserInfo> userInfos = List.of(
            UserInfo.builder().id("user1").build(),
            UserInfo.builder().id("user2").build()
        );
        when(keycloakCustomClient.getUsers(userIds)).thenReturn(userInfos);

        List<UserInfo> result = authService.getUsers(userIds);

        verify(keycloakCustomClient, times(1)).getUsers(userIds);
        assertThat(result)
            .extracting(UserInfo::getId)
            .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void getUsersWithAttributes() {
        List<String> userIds = List.of("id1", "id2");
        List<RegulatorUserInfoDTO> expectedUsers =
            List.of(RegulatorUserInfoDTO.builder().id("id1").build(),
                RegulatorUserInfoDTO.builder().id("id2").build());

        when(keycloakCustomClient.getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class))
            .thenReturn(expectedUsers);

        List<RegulatorUserInfoDTO> result = authService.getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class);

        verify(keycloakCustomClient, times(1)).getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class);
        assertThat(result)
            .extracting(RegulatorUserInfoDTO::getId)
            .containsExactlyInAnyOrder("id1", "id2");
    }

    @Test
    void getUserDetails() {
        String userId = "userId";
        UserDetails userDetails = UserDetails.builder()
            .id(userId)
            .signature(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("sign").build())
            .build();

        when(keycloakCustomClient.getUserDetails(userId)).thenReturn(Optional.of(userDetails));

        Optional<UserDetails> result = authService.getUserDetails(userId);

        assertThat(result).isNotEmpty().contains(userDetails);
        verify(keycloakCustomClient, times(1)).getUserDetails(userId);
    }

    @Test
    void getUserSignature() {
        String signatureUuid = UUID.randomUUID().toString();
        FileDTO signature = FileDTO.builder()
            .fileContent("content".getBytes())
            .fileName("signature")
            .fileSize(1L)
            .fileType("type")
            .build();

        when(keycloakCustomClient.getUserSignature(signatureUuid)).thenReturn(Optional.of(signature));

        Optional<FileDTO> result = authService.getUserSignature(signatureUuid);

        assertThat(result).isNotEmpty().contains(signature);
        verify(keycloakCustomClient, times(1)).getUserSignature(signatureUuid);
    }
    
    @Test
    void saveUser_update() {
        String userId = "user";
        String email = "email@email";
        final Map<String, List<String>> existingAttributes = new HashMap<>();
        existingAttributes.put("key1", List.of("val1"));
        existingAttributes.put("key2", List.of("val2"));

        UserRepresentation existingUserRepresentation = new UserRepresentation();
        existingUserRepresentation.setId(userId);
        existingUserRepresentation.setAttributes(existingAttributes);
        existingUserRepresentation.setEmail(email);

        final Map<String, List<String>> newAttributes = new HashMap<>();
        newAttributes.put("key2", List.of("val2_new"));
        newAttributes.put("key3", List.of("val3"));

        UserRepresentation newUserRepresentation = new UserRepresentation();
        newUserRepresentation.setId(userId);
        newUserRepresentation.setAttributes(newAttributes);
        newUserRepresentation.setEmail(email);

        mockFindByEmail(List.of(existingUserRepresentation), email);
        mockGetUserResource(userId, existingUserRepresentation);

        authService.saveUser(newUserRepresentation);

        verifyFindByEmail(email);
        verifyGetUserResource(userId);
        
        verify(keycloakProperties, times(2)).getRealm();
        verify(keycloakAdminClient, times(2)).realm(REALM);
        verify(realmResource, times(2)).users();
        
        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);

		verify(userResource, times(1)).update(userCaptor.capture());
		UserRepresentation userCaptured = userCaptor.getValue();
		assertThat(userCaptured.getAttributes()).containsExactlyInAnyOrderEntriesOf(Map.of(
				"key1", List.of("val1"),
				"key2", List.of("val2_new"),
				"key3", List.of("val3")
				));
    }

    @Test
    void saveUserDetails() throws UserDetailsSaveException {
        UserDetailsRequest userDetails = UserDetailsRequest.builder()
            .id("userId")
            .signature(
                SignatureRequest.builder().content("content".getBytes()).name("name").size(1L).type("type").build())
            .build();

        authService.saveUserDetails(userDetails);

        verify(keycloakCustomClient, times(1)).saveUserDetails(userDetails);
    }

    @Test
    void saveUserDetails_throws_UserDetailsSaveException() {
        UserDetailsRequest userDetails = UserDetailsRequest.builder()
            .id("userId")
            .signature(
                SignatureRequest.builder().content("content".getBytes()).name("name").size(1L).type("type").build())
            .build();

        doThrow(new RuntimeException("saving error")).when(keycloakCustomClient).saveUserDetails(userDetails);

        try {
            authService.saveUserDetails(userDetails);
            Assertions.fail("should not reach here");
        } catch (UserDetailsSaveException e) {
            assertThat(e.getCause().getMessage()).isEqualTo("saving error");
        } catch (Exception e) {
            Assertions.fail("should not throw different exception");
        }

        verify(keycloakCustomClient, times(1)).saveUserDetails(userDetails);
    }

	@Test
	void registerAndEnableUserAndSetPassword_already_enabled_throws_Exception() {
		String userId = "userId";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setId(userId);
		userRepresentation.setEnabled(Boolean.TRUE);

		BusinessException be = assertThrows(BusinessException.class,
				() -> authService.registerAndEnableUserAndSetPassword(userRepresentation, "pass"));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.USER_INVALID_STATUS);
	}

	@Test
	void registerAndEnableUserAndSetPassword() {
		String email = "email";
		String userPassword = "password";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);

		mockFindByEmail(Collections.emptyList(), email);
		String userIdCreated = "userId";
		mockRegisterUser_created(userRepresentation, userIdCreated);

		String result = authService.registerAndEnableUserAndSetPassword(userRepresentation, userPassword);

		assertThat(result).isEqualTo(userIdCreated);
		assertThat(userRepresentation.isEmailVerified()).isTrue();
		assertThat(userRepresentation.getCreatedTimestamp()).isNotNull();
		assertThat(userRepresentation.getCredentials().get(0).getValue()).isEqualTo(userPassword);

		verify(keycloakProperties, times(2)).getRealm();
		verify(keycloakAdminClient, times(2)).realm(REALM);
		verify(realmResource, times(2)).users();
		verify(usersResource, times(1)).create(userRepresentation);
		verify(response, times(1)).getStatus();
	}

	@Test
	void enableAndSaveUserAndSetPassword() {
		String email = "email";
		String userId = "userId";
		String userPassword = "password";

		UserRepresentation existingUserRepresentation = new UserRepresentation();
		existingUserRepresentation.setEmail(email);
		existingUserRepresentation.setId(userId);
		existingUserRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), "1");

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setId(userId);
		userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), "2");

		mockFindByEmail(List.of(existingUserRepresentation), email);
		mockGetUserResource(userId, existingUserRepresentation);

		authService.enableAndSaveUserAndSetPassword(userRepresentation, userPassword);

		assertThat(userRepresentation.isEmailVerified()).isTrue();
		assertThat(userRepresentation.getCreatedTimestamp()).isNotNull();
		assertThat(userRepresentation.getCredentials().get(0).getValue()).isEqualTo(userPassword);
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
				.containsExactly("2");

		verify(keycloakProperties, times(2)).getRealm();
		verify(keycloakAdminClient, times(2)).realm(REALM);
		verify(realmResource, times(2)).users();
		verify(usersResource, times(1)).search(email, true);
		verify(usersResource, times(1)).get(userId);
		verify(userResource, times(1)).toRepresentation();
		verify(userResource, times(1)).update(userRepresentation);
	}

	@Test
	void enableAndSaveUser() {
		String email = "email";
		String userId = "userId";

		UserRepresentation existingUserRepresentation = new UserRepresentation();
		existingUserRepresentation.setEmail(email);
		existingUserRepresentation.setId(userId);
		existingUserRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), "1");

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setId(userId);
		userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), "2");

		mockFindByEmail(List.of(existingUserRepresentation), email);
		mockGetUserResource(userId, existingUserRepresentation);

		authService.enableAndSaveUser(userRepresentation);

		assertThat(userRepresentation.isEmailVerified()).isTrue();
		assertThat(userRepresentation.getCreatedTimestamp()).isNotNull();
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
				.containsExactly("2");

		verify(keycloakProperties, times(2)).getRealm();
		verify(keycloakAdminClient, times(2)).realm(REALM);
		verify(realmResource, times(2)).users();
		verify(usersResource, times(1)).search(email, true);
		verify(usersResource, times(1)).get(userId);
		verify(userResource, times(1)).toRepresentation();
		verify(userResource, times(1)).update(userRepresentation);
	}

	@Test
	void enableUserAndSetPassword() {
		String userId = "userId";
		String userPassword = "password";
		String email = "email";

		UserRepresentation existingUserRepresentation = new UserRepresentation();
		existingUserRepresentation.setEmail(email);
		existingUserRepresentation.setId(userId);
		existingUserRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), "1");

		mockGetUserResource(userId, existingUserRepresentation);
		mockFindByEmail(List.of(existingUserRepresentation), email);

		authService.enableUserAndSetPassword(userId, userPassword);

		assertThat(existingUserRepresentation.isEmailVerified()).isTrue();
		assertThat(existingUserRepresentation.getCreatedTimestamp()).isNotNull();
		assertThat(existingUserRepresentation.getCredentials().get(0).getValue()).isEqualTo(userPassword);

		verify(keycloakProperties, times(3)).getRealm();
		verify(keycloakAdminClient, times(3)).realm(REALM);
		verify(realmResource, times(3)).users();
		verify(usersResource, times(1)).search(email, true);
		verify(usersResource, times(2)).get(userId);
		verify(userResource, times(2)).toRepresentation();
		verify(userResource, times(1)).update(existingUserRepresentation);
	}

    @Test
    void validateAuthenticatedUserOtp() {
        String otp = "otp";
        String accessToken = "accessToken";

        authService.validateAuthenticatedUserOtp(otp, accessToken);

        verify(keycloakCustomClient, times(1)).validateAuthenticatedUserOtp(otp, accessToken);
    }
    
    @Test
    void validateUnAuthenticatedUserOtp() {
        String otp = "otp";
        String email = "email";

        authService.validateUnAuthenticatedUserOtp(otp, email);

        verify(keycloakCustomClient, times(1)).validateUnauthenticatedUserOtp(otp, email);
    }

    @Test
    void deleteOtpCredentials() {
        final String userId = "userId";
        final String realm = "realm";
        final String credentialId = "id";
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType("otp");
        credentialRepresentation.setId(credentialId);

        // Mock
        when(keycloakProperties.getRealm()).thenReturn(realm);
        when(keycloakAdminClient.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(usersResource.get(userId).credentials()).thenReturn(List.of(credentialRepresentation));

        // Invoke
        authService.deleteOtpCredentials(userId);

        // Verify
        verify(usersResource.get(userId), times(1)).removeCredential(credentialId);
    }
    
    @Test
    void hasUserPassword() {
    	String userId = "user";
    	CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    	when(usersResource.get(userId)).thenReturn(userResource);
    	when(userResource.credentials()).thenReturn(List.of(credentialRepresentation));

    	boolean result = authService.hasUserPassword(userId);
    	assertThat(result).isTrue();

    	verify(usersResource, times(1)).get(userId);
    	verify(userResource, times(1)).credentials();
    }

	@Test
	void setUserPassword_has_already_password() {
		String userId = "user";
		String userPassword = "password";

		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue("dfdf");

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setId(userId);
		userRepresentation.setCredentials(List.of(credentialRepresentation));

		mockGetUserResource(userId, userRepresentation);
		mockGetUserCredentials(userId, List.of(credentialRepresentation));

		BusinessException be = assertThrows(BusinessException.class, () -> authService.setUserPassword(userId, userPassword));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.USER_INVALID_STATUS);

		verify(usersResource, times(2)).get(userId);
    	verify(userResource, times(1)).credentials();
	}

	@Test
	void setUserPassword() {
		String userId = "user";
		String userPassword = "password";
		String email = "email";

		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue("dfdf");

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setId(userId);
		userRepresentation.setEmail(email);

		mockGetUserResource(userId, userRepresentation);
		mockGetUserCredentials(userId, Collections.emptyList());
		mockFindByEmail(List.of(userRepresentation), email);

		authService.setUserPassword(userId, userPassword);

		assertThat(userRepresentation.getCredentials().get(0).getValue()).isEqualTo(userPassword);

		verify(usersResource, times(3)).get(userId);
		verify(usersResource, times(1)).search(email, true);
		verify(userResource, times(1)).update(userRepresentation);
	}
    
    @Test
    void resetUserPassword() {
        String currentPassword = "currentpassword";
        String newPassword = "newpassword";
        String otp = "otp";
        String email = "email";
        String userId = "user";

        CredentialRepresentation currentCredentialRepresentation = new CredentialRepresentation();
        currentCredentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        currentCredentialRepresentation.setValue(currentPassword);

        CredentialRepresentation newCredentialRepresentation = new CredentialRepresentation();
        newCredentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        newCredentialRepresentation.setValue(newPassword);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.setEmail(email);
        userRepresentation.setCredentials(List.of(currentCredentialRepresentation));

        mockGetUserCredentials(userId, List.of(currentCredentialRepresentation));
        mockFindByEmail(List.of(userRepresentation), email);
        mockGetUserResource(userId, userRepresentation);

        authService.resetUserPassword(userRepresentation, newPassword, otp);

        verify(keycloakCustomClient, times(1)).validateUnauthenticatedUserOtp(otp, email);

        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
		verify(userResource, times(1)).update(userCaptor.capture());
		UserRepresentation userCaptured = userCaptor.getValue();
		assertThat(userCaptured.getCredentials().get(0).getValue()).isEqualTo(newPassword);
    }

    @Test
    void resetUserPassword_user_has_no_password() {
    	String userId = "user";
        String password = "password";
        String otp = "otp";
        String email = "email";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.setEmail(email);

        mockGetUserCredentials(userId, Collections.emptyList());

        BusinessException businessException =
            assertThrows(BusinessException.class, 
            		() -> authService.resetUserPassword(userRepresentation, password, otp));

        assertEquals(ErrorCode.USER_INVALID_STATUS, businessException.getErrorCode());
    }
    
    @Test
    void deleteUserSessions() {
    	String userId = "user";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        UserSessionRepresentation session = new UserSessionRepresentation();
        session.setId(userId);

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(usersResource.get(userId).getUserSessions()).thenReturn(List.of(session, session, session));
        
        authService.deleteUserSessions(userId);
        
        verify(realmResource, times(3)).deleteSession(session.getId(), false);
    }

    private void mockRegisterUser_created(UserRepresentation userRepresentation, String userIdCreated) {
        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(userRepresentation)).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.CREATED.value());
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);
        when(response.getLocation()).thenReturn(URI.create("http://www.pmrv.uk/" + userIdCreated));
    }

    private void mockGetUserResource(String userId, UserRepresentation userRepresentation) {
		when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
	}

    private void verifyGetUserResource(String userId) {
    	verify(keycloakProperties, atLeastOnce()).getRealm();
        verify(keycloakAdminClient, atLeastOnce()).realm(REALM);
        verify(realmResource, atLeastOnce()).users();
        verify(usersResource, times(1)).get(userId);
        verify(userResource, times(1)).toRepresentation();
	}

    private void mockGetUserCredentials(String userId, List<CredentialRepresentation> credentials) {
		when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.credentials()).thenReturn(credentials);
	}

    private void mockFindByEmail(List<UserRepresentation> userRepresentations, String email) {
        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.search(email, true))
            .thenReturn(userRepresentations);
    }

    private void verifyFindByEmail(String email) {
    	verify(keycloakProperties, atLeastOnce()).getRealm();
        verify(keycloakAdminClient, atLeastOnce()).realm(REALM);
        verify(realmResource, atLeastOnce()).users();
        verify(usersResource, times(1)).search(email, true);
    }

}
