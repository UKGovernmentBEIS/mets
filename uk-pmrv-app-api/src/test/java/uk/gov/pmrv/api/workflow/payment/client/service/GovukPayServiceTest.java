package uk.gov.pmrv.api.workflow.payment.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.client.domain.CreatePaymentRequest;
import uk.gov.pmrv.api.workflow.payment.client.domain.Link;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentLinks;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentResponse;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentState;
import uk.gov.pmrv.api.workflow.payment.client.domain.enumeration.RestEndPointEnum;
import uk.gov.pmrv.api.workflow.payment.config.property.GovukPayProperties;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentStateInfo;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(ObjectMapper.class)
class GovukPayServiceTest {

    @InjectMocks
    private GovukPayService govukPayService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GovukPayProperties govukPayProperties;

    @Test
    void createPayment() {
        BigDecimal amount = BigDecimal.valueOf(852.36);
        Integer intAmount = 85236;
        String paymentRefNum = "AEM-1223-5";
        String description = "payment desc";
        String returnUrl = "payment_return_url";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        String apiKey = "api_key";
        String serviceUrl = "http://localhost:8080/";
        PaymentCreateInfo paymentCreateInfo = PaymentCreateInfo.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .description(description)
            .returnUrl(returnUrl)
            .competentAuthority(competentAuthority)
            .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(apiKey);

        CreatePaymentRequest payment = CreatePaymentRequest.builder()
            .amount(intAmount)
            .reference(paymentRefNum)
            .description(description)
            .returnUrl(returnUrl)
            .build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(serviceUrl)
                        .path(RestEndPointEnum.GOV_UK_CREATE_PAYMENT.getPath())
                        .build()
                        .toUri())
                .restEndPoint(RestEndPointEnum.GOV_UK_CREATE_PAYMENT)
                .headers(httpHeaders)
                .body(payment)
                .restTemplate(restTemplate)
                .build();

        String paymentId = "paymentId";
        String nextUrl = "payment_next_url";
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .paymentId(paymentId)
            .links(PaymentLinks.builder().nextUrl(Link.builder().href(nextUrl).build()).build())
            .build();

        when(govukPayProperties.getApiKeys()).thenReturn(
            Map.of(competentAuthority.name().toLowerCase(), apiKey));
        when(govukPayProperties.getServiceUrl()).thenReturn(serviceUrl);
        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(payment, httpHeaders),
                new ParameterizedTypeReference<PaymentResponse>() {}))
                .thenReturn(new ResponseEntity<>(paymentResponse, HttpStatus.OK));

        PaymentCreateResult paymentCreateResult = govukPayService.createPayment(paymentCreateInfo);

        assertNotNull(paymentCreateResult);
        assertEquals(paymentId, paymentCreateResult.getPaymentId());
        assertEquals(nextUrl, paymentCreateResult.getNextUrl());
    }

    @Test
    void createPayment_client_exception() {
        BigDecimal amount = BigDecimal.valueOf(852.36);
        Integer intAmount = 85236;
        String paymentRefNum = "AEM-1223-5";
        String description = "payment desc";
        String returnUrl = "payment_return_url";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        String apiKey = "api_key";
        String serviceUrl = "http://localhost:8080/";
        PaymentCreateInfo paymentCreateInfo = PaymentCreateInfo.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .description(description)
            .returnUrl(returnUrl)
            .competentAuthority(competentAuthority)
            .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(apiKey);

        CreatePaymentRequest payment = CreatePaymentRequest.builder()
            .amount(intAmount)
            .reference(paymentRefNum)
            .description(description)
            .returnUrl(returnUrl)
            .build();

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(serviceUrl)
                        .path(RestEndPointEnum.GOV_UK_CREATE_PAYMENT.getPath())
                        .build()
                        .toUri())
                .restEndPoint(RestEndPointEnum.GOV_UK_CREATE_PAYMENT)
                .headers(httpHeaders)
                .body(payment)
                .restTemplate(restTemplate)
                .build();

        when(govukPayProperties.getApiKeys()).thenReturn(
            Map.of(competentAuthority.name().toLowerCase(), apiKey));
        when(govukPayProperties.getServiceUrl()).thenReturn(serviceUrl);
        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.POST, new HttpEntity<>(payment, httpHeaders),
                new ParameterizedTypeReference<PaymentResponse>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> govukPayService.createPayment(paymentCreateInfo));

        assertEquals(ErrorCode.INTERNAL_SERVER, businessException.getErrorCode());
    }

    @Test
    void getPayment() {
        String paymentId = "n4brhul26f2hn1lt992ejj10ht";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(paymentId)
            .competentAuthority(competentAuthority)
            .build();
        String apiKey = "api_key";
        String serviceUrl = "http://localhost:8080/";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(apiKey);

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(serviceUrl)
                        .path(RestEndPointEnum.GOV_UK_GET_PAYMENT.getPath())
                        .build(paymentGetInfo.getPaymentId()))
                .restEndPoint(RestEndPointEnum.GOV_UK_GET_PAYMENT)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        PaymentState paymentState = PaymentState.builder()
            .status("success")
            .finished(true)
            .build();
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .paymentId(paymentId)
            .state(paymentState)
            .build();

        when(govukPayProperties.getApiKeys()).thenReturn(
            Map.of(competentAuthority.name().toLowerCase(), apiKey));
        when(govukPayProperties.getServiceUrl()).thenReturn(serviceUrl);
        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PaymentResponse>() {}))
                .thenReturn(new ResponseEntity<>(paymentResponse, HttpStatus.OK));

        PaymentGetResult paymentResult = govukPayService.getPayment(paymentGetInfo);

        assertNotNull(paymentResult);
        assertEquals(paymentId, paymentResult.getPaymentId());
        PaymentStateInfo paymentStateInfo = paymentResult.getState();
        assertNotNull(paymentStateInfo);
        assertEquals(paymentState.getStatus(), paymentStateInfo.getStatus());
    }

    @Test
    void getPayment_client_exception() {
        String paymentId = "n4brhul26f2hn1lt992ejj10ht";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(paymentId)
            .competentAuthority(competentAuthority)
            .build();
        String apiKey = "api_key";
        String serviceUrl = "http://localhost:8080/";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(apiKey);

        RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(serviceUrl)
                        .path(RestEndPointEnum.GOV_UK_GET_PAYMENT.getPath())
                        .build(paymentGetInfo.getPaymentId()))
                .restEndPoint(RestEndPointEnum.GOV_UK_GET_PAYMENT)
                .headers(httpHeaders)
                .restTemplate(restTemplate)
                .build();

        when(govukPayProperties.getApiKeys()).thenReturn(
            Map.of(competentAuthority.name().toLowerCase(), apiKey));
        when(govukPayProperties.getServiceUrl()).thenReturn(serviceUrl);
        when(restTemplate.exchange(appRestApi.getUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PaymentResponse>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> govukPayService.getPayment(paymentGetInfo));

        assertEquals(ErrorCode.INTERNAL_SERVER, businessException.getErrorCode());

    }
}