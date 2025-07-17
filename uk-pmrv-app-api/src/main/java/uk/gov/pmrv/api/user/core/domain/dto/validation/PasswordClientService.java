package uk.gov.pmrv.api.user.core.domain.dto.validation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.pmrv.api.user.core.domain.enumeration.RestEndPointEnum;

import java.util.Collections;

/**
 *  The client for <a href="https://haveibeenpwned.com/API/v3#PwnedPasswords">...</a>
 */
@Service
public class PasswordClientService {

    /** The {@link RestTemplate} */
    private final RestTemplate restTemplate;

    /** The {@link PwnedPasswordProperties} */
    private final PwnedPasswordProperties pwnedPasswordProperties;

    /**
     * The PasswordClient constructor
     *
     * @param restTemplate {@link RestTemplate}
     * @param pwnedPasswordProperties {@link PwnedPasswordProperties}
     */
    public PasswordClientService(
            RestTemplate restTemplate,
            PwnedPasswordProperties pwnedPasswordProperties) {
        this.restTemplate = restTemplate;
        this.pwnedPasswordProperties = pwnedPasswordProperties;
    }

    /**
     * Call to https://api.pwnedpasswords.com/range/{passwordHash} API
     *
     * @param passwordHash first 5 digits of password hash
     * @return list of password hash suffixes that match the input along with their occurrences
     * e.g. 1AA8423017483440CC271B810DEB524E139:5454
     */
    public String searchPassword(String passwordHash) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("text/plain")));

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(pwnedPasswordProperties.getServiceUrl())
                        .path(RestEndPointEnum.PWNED_PASSWORDS.getPath())
                        .build(passwordHash))
                .restEndPoint(RestEndPointEnum.PWNED_PASSWORDS)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        ResponseEntity<String> res = appRestApi.performApiCall();

        return res.getBody();
    }

}