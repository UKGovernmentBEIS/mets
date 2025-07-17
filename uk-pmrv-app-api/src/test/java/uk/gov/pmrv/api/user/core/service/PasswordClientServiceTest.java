package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PasswordClientService;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PwnedPasswordProperties;
import uk.gov.pmrv.api.user.core.domain.enumeration.RestEndPointEnum;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordClientServiceTest {

    @InjectMocks
    private PasswordClientService passwordClientService;

    @Mock
    private PwnedPasswordProperties pwnedPasswordProperties;

    @Mock
    private RestTemplate restTemplate;

    private static final String baseUrl = "https://api.pwnedpasswords.com";

    private static final String strongPasswordHashPrefix = "c7481";

    private static final String strongPasswordResponse = "1AA8423017483440CC271B810DEB524E139:2";

    @Test
    void searchPassword() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("text/plain")));

        when(pwnedPasswordProperties.getServiceUrl()).thenReturn(baseUrl);

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(baseUrl)
                        .path(RestEndPointEnum.PWNED_PASSWORDS.getPath())
                        .build(strongPasswordHashPrefix))
                .restEndPoint(RestEndPointEnum.PWNED_PASSWORDS)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), RestEndPointEnum.PWNED_PASSWORDS.getMethod(),
                new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<String>() {}))
                .thenReturn(new ResponseEntity<>(strongPasswordResponse, HttpStatus.OK));

        passwordClientService.searchPassword(strongPasswordHashPrefix);

        verify(pwnedPasswordProperties, times(1)).getServiceUrl();
        verify(restTemplate, times(1)).exchange(appRestApi.getUri(),
                RestEndPointEnum.PWNED_PASSWORDS.getMethod(), new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<String>() {});
    }

    @Test
    void searchPasswordThrowsException() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("text/plain")));

        when(pwnedPasswordProperties.getServiceUrl()).thenReturn(baseUrl);

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(baseUrl)
                        .path(RestEndPointEnum.PWNED_PASSWORDS.getPath())
                        .build(strongPasswordHashPrefix))
                .restEndPoint(RestEndPointEnum.PWNED_PASSWORDS)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        when(restTemplate.exchange(appRestApi.getUri(), RestEndPointEnum.PWNED_PASSWORDS.getMethod(),
                new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<String>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(Exception.class, () -> passwordClientService.searchPassword(strongPasswordHashPrefix));

        verify(pwnedPasswordProperties, times(1)).getServiceUrl();
        verify(restTemplate, times(1)).exchange(appRestApi.getUri(),
                RestEndPointEnum.PWNED_PASSWORDS.getMethod(), new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<String>() {});
    }
}
