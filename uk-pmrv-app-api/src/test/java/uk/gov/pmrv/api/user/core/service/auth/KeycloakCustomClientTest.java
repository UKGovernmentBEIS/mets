package uk.gov.pmrv.api.user.core.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.config.KeycloakProperties;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakRestEndPointEnum;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.core.SignatureRequest;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakSignature;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetails;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserOtpValidationInfo;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Import(ObjectMapper.class)
class KeycloakCustomClientTest {

    @InjectMocks
    private KeycloakCustomClient client;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KeycloakProperties keycloakProperties;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Keycloak keycloakAdminClient;

    private String authServerUrl = "http://serverurl";
    private String realm = "realm";

    @BeforeEach
    void init() {
        when(keycloakProperties.getAuthServerUrl()).thenReturn(authServerUrl);
        when(keycloakProperties.getRealm()).thenReturn(realm);
    }

    @Test
    void getUserDetails() {
        String userId = "userId";
        String token = "token";

        when(keycloakAdminClient.tokenManager().grantToken().getToken()).thenReturn(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(realmEndpointUrl())
                        .path(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS.getPath())
                        .queryParam("userId", "{userId}")
                        .build(userId))
                .restEndPoint(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        String signatureUUID = UUID.randomUUID().toString();
        KeycloakUserDetails keycloakUserDetails = KeycloakUserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder().name("signature").uuid(signatureUUID).build())
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakUserDetails>() {}))
                .thenReturn(new ResponseEntity<>(keycloakUserDetails, HttpStatus.OK));

        Optional<UserDetails> result = client.getUserDetails(userId);
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(UserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder()
                        .name("signature").uuid(signatureUUID)
                        .build()).build());

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakUserDetails>() {});
    }

    @Test
    void getUserDetails_when_request_throws_exception() {
        String userId = "userId";
        String token = "token";

        when(keycloakAdminClient.tokenManager().grantToken().getToken()).thenReturn(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(realmEndpointUrl())
                        .path(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS.getPath())
                        .queryParam("userId", "{userId}")
                        .build(userId))
                .restEndPoint(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakUserDetails>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            client.getUserDetails(userId);
            Assertions.fail("Should not reach here");
        } catch (RuntimeException e) {
            assertThat(e.getCause().getClass()).isEqualTo(HttpClientErrorException.class);
        } catch (Exception e) {
            Assertions.fail("Should not reach here");
        }

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakUserDetails>() {});
    }

    @Test
    void saveUserDetails() {
        String userId = "userId";
        SignatureRequest signature = SignatureRequest.builder().content("content".getBytes()).name("name").size(3L).type("type").build();
        UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();

        String token = "token";
        when(keycloakAdminClient.tokenManager().grantToken().getToken()).thenReturn(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        KeycloakUserDetailsRequest keycloakUserDetailsRequest = KeycloakUserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(realmEndpointUrl())
                        .path(KeycloakRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS)
                .headers(httpHeaders)
                .body(keycloakUserDetailsRequest)
                .restTemplate(restTemplate)
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders),
                new ParameterizedTypeReference<Void>() {}))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));


        client.saveUserDetails(userDetailsRequest);

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders),
                new ParameterizedTypeReference<Void>() {});
    }

    @Test
    void saveUserDetails_when_request_throws_exception() {
        String userId = "userId";
        SignatureRequest signature = SignatureRequest.builder().content("content".getBytes()).name("name").size(3L).type("type").build();
        UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();

        String token = "token";
        when(keycloakAdminClient.tokenManager().grantToken().getToken()).thenReturn(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        KeycloakUserDetailsRequest keycloakUserDetailsRequest = KeycloakUserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(realmEndpointUrl())
                        .path(KeycloakRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS)
                .headers(httpHeaders)
                .body(keycloakUserDetailsRequest)
                .restTemplate(restTemplate)
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders),
                new ParameterizedTypeReference<Void>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            client.saveUserDetails(userDetailsRequest);
            Assertions.fail("Should not reach here");
        } catch (RuntimeException e) {
            assertThat(e.getCause().getClass()).isEqualTo(HttpClientErrorException.class);
        } catch (Exception e) {
            Assertions.fail("Should not reach here");
        }

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders),
                new ParameterizedTypeReference<Void>() {});
    }

    @Test
    void getUserSignature() {
        String signatureUuid = UUID.randomUUID().toString();
        String token = "token";

        when(keycloakAdminClient.tokenManager().grantToken().getToken()).thenReturn(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        KeycloakSignature keycloakSignature = KeycloakSignature.builder()
                .name("name").content("content".getBytes()).size(1L).type("type")
                .build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(realmEndpointUrl())
                        .path(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE.getPath())
                        .queryParam("signatureUuid", "{signatureUuid}")
                        .build(signatureUuid))
                .restEndPoint(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        FileDTO fileDTO = FileDTO.builder()
                .fileName("name").fileContent("content".getBytes()).fileSize(1L).fileType("type")
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakSignature>() {}))
                .thenReturn(new ResponseEntity<>(keycloakSignature, HttpStatus.OK));

        Optional<FileDTO> result = client.getUserSignature(signatureUuid);
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(fileDTO);

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakSignature>() {});
    }

    @Test
    void getUserSignature_when_request_throws_exception() {
        String signatureUuid = UUID.randomUUID().toString();
        String token = "token";

        when(keycloakAdminClient.tokenManager().grantToken().getToken()).thenReturn(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(realmEndpointUrl())
                        .path(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE.getPath())
                        .queryParam("signatureUuid", "{signatureUuid}")
                        .build(signatureUuid))
                .restEndPoint(KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakSignature>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        try{
            client.getUserSignature(signatureUuid);
            Assertions.fail("Should not reach here");
        } catch (RuntimeException e) {
            assertThat(e.getCause().getClass()).isEqualTo(HttpClientErrorException.class);
        } catch (Exception e) {
            Assertions.fail("Should not reach here");
        }

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<KeycloakSignature>() {});
    }

    @Test
    void validateUnauthenticatedUserOtp() {
        String otp = "otp";
        String email = "email";

        KeycloakUserOtpValidationInfo otpValidation = KeycloakUserOtpValidationInfo.builder()
        		.otp(otp)
        		.email(email)
        		.build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(realmEndpointUrl())
                        .path(KeycloakRestEndPointEnum.KEYCLOAK_VALIDATE_OTP.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakRestEndPointEnum.KEYCLOAK_VALIDATE_OTP)
                .body(otpValidation)
                .restTemplate(restTemplate)
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(otpValidation),
                new ParameterizedTypeReference<Void>() {}))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        client.validateUnauthenticatedUserOtp(otp, email);

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(otpValidation),
                new ParameterizedTypeReference<Void>() {});
    }

    private String realmEndpointUrl() {
        return authServerUrl + "/realms/" + realm;
    }
}
