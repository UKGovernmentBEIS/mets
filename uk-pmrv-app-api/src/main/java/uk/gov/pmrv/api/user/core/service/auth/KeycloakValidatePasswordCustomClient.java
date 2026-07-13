package uk.gov.pmrv.api.user.core.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.common.utils.KeycloakCustomClientUtilsProvider;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakValidatePasswordRestEndPointEnum;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationResponse;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakValidatePasswordRequest;

@Log4j2
@Component
@RequiredArgsConstructor
public class KeycloakValidatePasswordCustomClient {

    private final RestTemplate restTemplate;
    private final KeycloakCustomClientUtilsProvider keycloakCustomClientUtilsProvider;


    public KeycloakPasswordValidationResponse validatePassword(String password) {

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(keycloakCustomClientUtilsProvider.realmEndpointUrl())
                        .path(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD.getPath())
                        .build()
                        .toUri())
                .restEndPoint(KeycloakValidatePasswordRestEndPointEnum.KEYCLOAK_VALIDATE_PASSWORD)
                .headers(keycloakCustomClientUtilsProvider.httpHeaders())
                .body(KeycloakValidatePasswordRequest.builder().password(password).build())
                .restTemplate(restTemplate)
                .build();

        ResponseEntity<KeycloakPasswordValidationResponse> res;
        try {
            res = appRestApi.performApiCall();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e);
        }

        return res.getBody();
    }

}
