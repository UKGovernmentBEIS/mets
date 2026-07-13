package uk.gov.pmrv.api.user.core.service.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.common.utils.KeycloakCustomClientUtilsProvider;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakValidatePasswordRestEndPointEnum;
import uk.gov.pmrv.api.user.core.domain.enumeration.PasswordPolicyErrorCodeEnum;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationError;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationResponse;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakValidatePasswordRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakValidatePasswordCustomClientTest {

    private static final String AUTH_SERVER_URL = "http://serverurl/realms/realm";

    @InjectMocks
    private KeycloakValidatePasswordCustomClient client;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KeycloakCustomClientUtilsProvider keycloakCustomClientUtilsProvider;

    @Test
    void validatePassword() {
        String password = "P@ssword123!";
        String token = "token";

        HttpHeaders httpHeaders = httpHeaders(token);
        KeycloakValidatePasswordRequest request = KeycloakValidatePasswordRequest.builder().password(password).build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(AUTH_SERVER_URL)
                        .path(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD)
                .headers(httpHeaders)
                .body(request)
                .restTemplate(restTemplate)
                .build();

        KeycloakPasswordValidationResponse response = KeycloakPasswordValidationResponse.builder()
                .valid(true)
                .errors(List.of())
                .build();

        when(keycloakCustomClientUtilsProvider.realmEndpointUrl()).thenReturn(AUTH_SERVER_URL);
        when(keycloakCustomClientUtilsProvider.httpHeaders()).thenReturn(httpHeaders);
        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<KeycloakPasswordValidationResponse>() {}))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        KeycloakPasswordValidationResponse result = client.validatePassword(password);

        assertThat(result).isEqualTo(response);

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<KeycloakPasswordValidationResponse>() {});
        verify(keycloakCustomClientUtilsProvider).realmEndpointUrl();
        verify(keycloakCustomClientUtilsProvider).httpHeaders();
        verifyNoMoreInteractions(keycloakCustomClientUtilsProvider);
    }

    @Test
    void validatePassword_with_violations() {
        String password = "short";
        String token = "token";

        HttpHeaders httpHeaders = httpHeaders(token);
        KeycloakValidatePasswordRequest request = KeycloakValidatePasswordRequest.builder().password(password).build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(AUTH_SERVER_URL)
                        .path(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD)
                .headers(httpHeaders)
                .body(request)
                .restTemplate(restTemplate)
                .build();

        KeycloakPasswordValidationResponse response = KeycloakPasswordValidationResponse.builder()
                .valid(false)
                .errors(List.of(KeycloakPasswordValidationError.builder()
                        .code(PasswordPolicyErrorCodeEnum.INVALID_MIN_LENGTH)
                        .message("Password is too short")
                        .build()))
                .build();

        when(keycloakCustomClientUtilsProvider.realmEndpointUrl()).thenReturn(AUTH_SERVER_URL);
        when(keycloakCustomClientUtilsProvider.httpHeaders()).thenReturn(httpHeaders);
        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<KeycloakPasswordValidationResponse>() {}))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        KeycloakPasswordValidationResponse result = client.validatePassword(password);

        assertThat(result).isEqualTo(response);

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<KeycloakPasswordValidationResponse>() {});
        verify(keycloakCustomClientUtilsProvider).realmEndpointUrl();
        verify(keycloakCustomClientUtilsProvider).httpHeaders();
        verifyNoMoreInteractions(keycloakCustomClientUtilsProvider);
    }

    @Test
    void validatePassword_when_request_throws_exception() {
        String password = "P@ssword123!";
        String token = "token";

        HttpHeaders httpHeaders = httpHeaders(token);
        KeycloakValidatePasswordRequest request = KeycloakValidatePasswordRequest.builder().password(password).build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(AUTH_SERVER_URL)
                        .path(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD)
                .headers(httpHeaders)
                .body(request)
                .restTemplate(restTemplate)
                .build();

        when(keycloakCustomClientUtilsProvider.realmEndpointUrl()).thenReturn(AUTH_SERVER_URL);
        when(keycloakCustomClientUtilsProvider.httpHeaders()).thenReturn(httpHeaders);
        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<KeycloakPasswordValidationResponse>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            client.validatePassword(password);
            Assertions.fail("Should not reach here");
        } catch (RuntimeException e) {
            assertThat(e.getCause().getClass()).isEqualTo(HttpClientErrorException.class);
        } catch (Exception e) {
            Assertions.fail("Should not reach here");
        }

        verify(restTemplate, times(1)).exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<KeycloakPasswordValidationResponse>() {});
        verify(keycloakCustomClientUtilsProvider).realmEndpointUrl();
        verify(keycloakCustomClientUtilsProvider).httpHeaders();
        verifyNoMoreInteractions(keycloakCustomClientUtilsProvider);
    }

    private static HttpHeaders httpHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        return httpHeaders;
    }
}
