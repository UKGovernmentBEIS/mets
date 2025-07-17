package uk.gov.pmrv.api.user.core.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.netz.api.userinfoapi.UserInfoApi;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.transform.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService implements UserInfoApi {

    private final AuthService authService;
    private final UserMapper userMapper;

    public UserInfoDTO getUserByUserId(String userId) {
        return userMapper.toUserInfoDTO(authService.getUserRepresentationById(userId));
    }

    public Optional<UserInfoDTO> getUserByEmail(String email) {
        return authService
                .getByEmail(email)
                .map(userMapper::toUserInfoDTO);
    }
    
    public List<UserInfo> getUsers(List<String> userIds) {
        return authService.getUsers(userIds);
    }
    
    public <T> List<T> getUsersWithAttributes(List<String> userIds, Class<T> attributesClazz) {
        return authService.getUsersWithAttributes(userIds, attributesClazz);
    }
    
    public Optional<UserDetails> getUserDetails(String userId) {
        return authService.getUserDetails(userId);
    }
    
    public Optional<FileDTO> getUserSignature(String signatureUuid) {
        return authService.getUserSignature(signatureUuid);
    }

    public void enableUserAndSetPassword(String userId, String password) {
        authService.enableUserAndSetPassword(userId, password);
    }
    
    public void setUserPassword(String userId, String password) {
        authService.setUserPassword(userId, password);
    }

    public void validateAuthenticatedUserOtp(String otp, String token) {
        authService.validateAuthenticatedUserOtp(otp, token);
    }

    public void deleteOtpCredentialsByEmail(String email) {
        authService.getByEmail(email)
                .ifPresentOrElse(userRepresentation -> deleteOtpCredentials(userRepresentation.getId()),
                        () -> {throw new BusinessException(ErrorCode.USER_NOT_EXIST);});
    }

	public void deleteOtpCredentials(String userId) {
		authService.deleteOtpCredentials(userId);
		authService.deleteUserSessions(userId);
	}

	public void resetPassword(String email, String otp, String password) {
		authService.getByEmail(email)
		        .ifPresentOrElse(userRepresentation -> {
		            authService.resetUserPassword(userRepresentation, password, otp);
		            authService.deleteUserSessions(userRepresentation.getId());
		            },
                       () -> {throw new BusinessException(ErrorCode.USER_NOT_EXIST);});
	}
	
	/**
     * Returns whether a password has been set in keycloak for the {@code userId}.
     * @param userId the user id
     * @return true/false
     */
    public boolean hasUserPassword(String userId) {
        return authService.hasUserPassword(userId);
    }
}
