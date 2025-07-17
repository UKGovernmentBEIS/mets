package uk.gov.pmrv.api.workflow.payment.client.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.netz.api.restclient.RestClientEndPoint;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentResponse;

/**
 * The GOV UK Pay rest points enum.
 */
@Getter
@AllArgsConstructor
public enum RestEndPointEnum implements RestClientEndPoint {

    /** Return information about the payment. The Authorization token needs to be specified in the 'authorization' header as 'authorization: Bearer YOUR_API_KEY_HERE'. */
    GOV_UK_GET_PAYMENT("/v1/payments/{paymentId}", HttpMethod.GET, new ParameterizedTypeReference<PaymentResponse>() {}),

    /** Create a new payment for the account associated to the Authorisation token. */
    GOV_UK_CREATE_PAYMENT("/v1/payments", HttpMethod.POST, new ParameterizedTypeReference<PaymentResponse>() {});

    private final String path;
    private final HttpMethod method;
    private final ParameterizedTypeReference<?> parameterizedTypeReference;
}
