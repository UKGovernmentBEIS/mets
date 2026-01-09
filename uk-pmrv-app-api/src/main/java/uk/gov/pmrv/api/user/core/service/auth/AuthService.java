package uk.gov.pmrv.api.user.core.service.auth;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.pmrv.api.user.core.config.KeycloakProperties;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Gateway to Keycloak related services.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakCustomClient keycloakCustomClient;
    private final KeycloakProperties keycloakProperties;
    private final Clock clock;

    public UserRepresentation getUserRepresentationById(String userId) {
        return getUsersResource().get(userId).toRepresentation();
    }
    
    public List<CredentialRepresentation> getUserCredentials(String userId) {
        return getUsersResource().get(userId).credentials();
    }

    /**
     * Finds the user in the Keycloak db with the provided email.
     * For our use case we have made the assumption that email is the same as username in keycloak
     * and we are searching by username because keycloak supports exact matches instead of fuzzy
     * searches when searching by email.
     * @param email the email based on which the search will be done
     * @return an Optional of {@link UserRepresentation}
     * @since v.0.1.0
     */
    public Optional<UserRepresentation> getByEmail(String email) {
        return getByUsername(email);
    }

    public Optional<UserRepresentation> getByUsername(String username) {
        List<UserRepresentation> users = getUsersResource().search(username, true);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }
    
    /**
     * Finds the users in Keycloak db with the provided user ids.
     * @param userIds the user ids based on which the search will be done
     * @return list of UserInfo
     */
    public List<UserInfo> getUsers(List<String> userIds) {
        return keycloakCustomClient.getUsers(userIds);
    }

    /**
     * Finds the users in Keycloak db with the provided user ids.
     * @param userIds the user ids based on which the search will be done
     * @param attributesClazz Representation Object
     * @return List of users
     */
    public <T> List<T> getUsersWithAttributes(List<String> userIds, Class<T> attributesClazz) {
        return keycloakCustomClient.getUsersWithAttributes(userIds, attributesClazz);
    }
    
    public Optional<UserDetails> getUserDetails(String userId) {
        return keycloakCustomClient.getUserDetails(userId);
    }
    
    public Optional<FileDTO> getUserSignature(String signatureUuid) {
        return keycloakCustomClient.getUserSignature(signatureUuid);
    }

    public void saveUserDetails(UserDetailsRequest userDetails) throws UserDetailsSaveException {
        try{
            keycloakCustomClient.saveUserDetails(userDetails);
        } catch (Exception e) {
            throw new UserDetailsSaveException(e);
        }
    }
    
    public String registerAndEnableUserAndSetPassword(UserRepresentation userRepresentation, String userPassword) {
    	enableUser(userRepresentation);
    	setUserPassword(userRepresentation, userPassword);
        return saveUser(userRepresentation);
    }

    public void enableAndSaveUserAndSetPassword(UserRepresentation userRepresentation, String userPassword){
    	enableUser(userRepresentation);
    	setUserPassword(userRepresentation, userPassword);
    	saveUser(userRepresentation);
    }

    public void enableAndSaveUser(UserRepresentation userRepresentation){
    	enableUser(userRepresentation);
    	saveUser(userRepresentation);
    }

    public void enableUserAndSetPassword(String userId, String userPassword){
    	UserRepresentation userRepresentation = getUserRepresentationById(userId);
    	enableUser(userRepresentation);
    	setUserPassword(userRepresentation, userPassword);
    	saveUser(userRepresentation);
    }

    public void validateAuthenticatedUserOtp(String otp, String accessToken) {
        keycloakCustomClient.validateAuthenticatedUserOtp(otp, accessToken);
    }
    
    public void validateUnAuthenticatedUserOtp(String otp, String email) {
        keycloakCustomClient.validateUnauthenticatedUserOtp(otp, email);
    }

    public void deleteOtpCredentials(String userId) {
        getUserCredentials(userId).stream()
                .filter(credentialRepresentation -> credentialRepresentation.getType().equals("otp")).findFirst()
                .ifPresent(optCredential -> getUsersResource().get(userId).removeCredential(optCredential.getId()));
    }
    
    /**
     * Returns whether a password has been set in keycloak for the {@code userId}.
     * @param userId the user id
     * @return true/false
     */
    public boolean hasUserPassword(String userId) {
        List<CredentialRepresentation> userCredentials = getUserCredentials(userId);
		return !userCredentials.isEmpty()
				&& userCredentials.stream().anyMatch(cr -> CredentialRepresentation.PASSWORD.equals(cr.getType()));
    }

    public UserRepresentation setUserPassword(String userId, String userPassword){
    	UserRepresentation userRepresentation = getUserRepresentationById(userId);
    	if(hasUserPassword(userRepresentation.getId())) {
    		throw new BusinessException(ErrorCode.USER_INVALID_STATUS);
    	}
    	setUserPassword(userRepresentation, userPassword);
        saveUser(userRepresentation);
        return userRepresentation;
    }

    public void resetUserPassword(UserRepresentation userRepresentation, String password, String otp) {
		if(!hasUserPassword(userRepresentation.getId())) {
    		throw new BusinessException(ErrorCode.USER_INVALID_STATUS);
    	}
        validateUnAuthenticatedUserOtp(otp, userRepresentation.getEmail());
        setUserPassword(userRepresentation, password);
        saveUser(userRepresentation);
    }
    
	public void deleteUserSessions(String userId) {
		getUsersResource().get(userId).getUserSessions()
		.forEach(session -> deleteSession(session.getId()));		
	}

	public String saveUser(UserRepresentation userRepresentation) {
		Optional<UserRepresentation> existingKeycloakUserOpt = getByEmail(userRepresentation.getEmail());

		if(existingKeycloakUserOpt.isPresent()) {
			final String userId = existingKeycloakUserOpt.get().getId();
			final UserResource existingUserResource = getUsersResource().get(userId);
			final UserRepresentation existingUserRepresentation = existingUserResource.toRepresentation();
			userRepresentation.setAttributes(mergeUserAttributes(
					existingUserRepresentation.getAttributes() == null ? new HashMap<>() : existingUserRepresentation.getAttributes(),
					userRepresentation.getAttributes() == null ? new HashMap<>() : userRepresentation.getAttributes()));
			existingUserResource.update(userRepresentation);
        	return userId;
		} else {
			try (Response res = getUsersResource().create(userRepresentation)) {
				if (HttpStatus.valueOf(res.getStatus()) == HttpStatus.CREATED) {
					return CreatedResponseUtil.getCreatedId(res);
				} else {
					throw new BusinessException(ErrorCode.USER_REGISTRATION_FAILED_500);
				}
			}
		}
    }
	
	private Map<String, List<String>> mergeUserAttributes(Map<String, List<String>> existingAttributes,
			Map<String, List<String>> updatedAttributes){
		Map<String, List<String>> mergedAttributes = new HashMap<>(existingAttributes);
		updatedAttributes.forEach((key, value) -> mergedAttributes.put(key, value));
		return mergedAttributes;
	}

	private void enableUser(UserRepresentation userRepresentation) {
		if(BooleanUtils.isTrue(userRepresentation.isEnabled())) {
			throw new BusinessException(ErrorCode.USER_INVALID_STATUS);
		}

		userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setCreatedTimestamp(ZonedDateTime.now(clock).toInstant().toEpochMilli());
	}

    /**
     * Set user's password in keycloak.
     * @param userRepresentation userRepresentation {@link UserRepresentation}
     * @param password the password
     */
    private void setUserPassword(UserRepresentation userRepresentation, String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        userRepresentation.setCredentials(Collections.singletonList(credentials));
    }

    private UsersResource getUsersResource() {
    	return keycloakAdminClient.realm(keycloakProperties.getRealm()).users();
    }

    private void deleteSession(String sessionId) {
        keycloakAdminClient.realm(keycloakProperties.getRealm()).deleteSession(sessionId, false);
    }

}
