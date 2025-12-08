package uk.gov.pmrv.api.user.core.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.KeycloakCustomClientUtilsProvider;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserRestEndPointEnum;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakSignature;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetails;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserInfo;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserOtpValidationInfo;
import uk.gov.pmrv.api.user.core.transform.KeycloakUserMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
class KeycloakUserCustomClient {

    private final RestTemplate restTemplate;
    private final KeycloakCustomClientUtilsProvider  keycloakCustomClientUtilsProvider;
    private final KeycloakUserMapper keycloakUserMapper = Mappers.getMapper(KeycloakUserMapper.class);
    private final ObjectMapper objectMapper;

    public List<UserInfo> getUsers(List<String> userIds) {
        Optional<List<KeycloakUserInfo>> usersInfo = performGetUsersApiCall(userIds, false);
        return usersInfo.stream()
                .flatMap(Collection::stream)
                .map(keycloakUserMapper::toUserInfo)
                .collect(Collectors.toList());
    }

    public <T> List<T> getUsersWithAttributes(List<String> userIds, Class<T> attributesClazz) {
        Optional<List<KeycloakUserInfo>> usersInfo = performGetUsersApiCall(userIds, true);
        return usersInfo.stream()
                .flatMap(Collection::stream)
                .map(u -> objectMapper.convertValue(u, attributesClazz))
                .collect(Collectors.toList());
    }

    public Optional<UserDetails> getUserDetails(String userId) {
        if(userId == null) {
            return Optional.empty();
        }

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(keycloakCustomClientUtilsProvider.realmEndpointUrl())
                        .path(KeycloakUserRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS.getPath())
                        .queryParam("userId", "{userId}")
                        .build(userId))
                .restEndPoint(KeycloakUserRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS)
                .headers(keycloakCustomClientUtilsProvider.httpHeaders())
                .restTemplate(restTemplate)
                .build();

        ResponseEntity<KeycloakUserDetails> res;
        try{
            res = appRestApi.performApiCall();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        Optional<KeycloakUserDetails> userDetails = res.getBody() == null ? Optional.empty()
                : Optional.of(res.getBody());

        return userDetails.map(u -> UserDetails.builder()
                .id(u.getId())
                .signature(u.getSignature())
                .build());
    }

    public void saveUserDetails(UserDetailsRequest userDetails) {
    	RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(keycloakCustomClientUtilsProvider.realmEndpointUrl())
                        .path(KeycloakUserRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakUserRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS)
                .headers(keycloakCustomClientUtilsProvider.httpHeaders())
                .body(KeycloakUserDetailsRequest.builder()
                        .id(userDetails.getId())
                        .signature(userDetails.getSignature())
                        .build())
                .restTemplate(restTemplate)
                .build();

        try {
            appRestApi.performApiCall();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Optional<FileDTO> getUserSignature(String signatureUuid) {
        if(signatureUuid == null) {
            return Optional.empty();
        }

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(keycloakCustomClientUtilsProvider.realmEndpointUrl())
                        .path(KeycloakUserRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE.getPath())
                        .queryParam("signatureUuid", "{signatureUuid}")
                        .build(signatureUuid))
                .restEndPoint(KeycloakUserRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE)
                .headers(keycloakCustomClientUtilsProvider.httpHeaders())
                .restTemplate(restTemplate)
                .build();

        ResponseEntity<KeycloakSignature> res;
        try{
            res = appRestApi.performApiCall();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        Optional<KeycloakSignature> signature = res.getBody() == null ? Optional.empty()
                : Optional.of(res.getBody());

        return signature.map(s -> FileDTO.builder()
                .fileName(s.getName())
                .fileContent(s.getContent())
                .fileType(s.getType())
                .fileSize(s.getSize())
                .build());
    }

    public void validateAuthenticatedUserOtp(String otp, String token) {
    	RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(keycloakCustomClientUtilsProvider.realmEndpointUrl())
                        .path(KeycloakUserRestEndPointEnum.KEYCLOAK_VALIDATE_OTP.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakUserRestEndPointEnum.KEYCLOAK_VALIDATE_OTP)
                .headers(keycloakCustomClientUtilsProvider.buildHttpHeadersWithAuthToken(token))
                .body(KeycloakUserOtpValidationInfo.builder().otp(otp).build())
                .restTemplate(restTemplate)
                .build();

        try {
            appRestApi.performApiCall();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_OTP, e);
        }
    }

    public void validateUnauthenticatedUserOtp(String otp, String email) {
    	RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(keycloakCustomClientUtilsProvider.realmEndpointUrl())
                        .path(KeycloakUserRestEndPointEnum.KEYCLOAK_VALIDATE_OTP.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakUserRestEndPointEnum.KEYCLOAK_VALIDATE_OTP)
                .body(KeycloakUserOtpValidationInfo.builder().otp(otp).email(email).build())
                .restTemplate(restTemplate)
                .build();

        try {
            appRestApi.performApiCall();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_OTP, e);
        }
    }

    private Optional<List<KeycloakUserInfo>> performGetUsersApiCall(List<String> userIds, boolean includeAttributes) {
        if (userIds.isEmpty()) {
            return  Optional.empty();
        }

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(keycloakCustomClientUtilsProvider.realmEndpointUrl())
                        .path(KeycloakUserRestEndPointEnum.KEYCLOAK_GET_USERS.getPath())
                        .queryParam("includeAttributes", "{includeAttributes}")
                        .build(Boolean.toString(includeAttributes)))
                .restEndPoint(KeycloakUserRestEndPointEnum.KEYCLOAK_GET_USERS)
                .headers(keycloakCustomClientUtilsProvider.httpHeaders())
                .body(userIds)
                .restTemplate(restTemplate)
                .build();

        ResponseEntity<List<KeycloakUserInfo>> res;
        try{
            res = appRestApi.performApiCall();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e);
        }
        return res.getBody() == null ? Optional.empty() : Optional.of(res.getBody());
    }

}
