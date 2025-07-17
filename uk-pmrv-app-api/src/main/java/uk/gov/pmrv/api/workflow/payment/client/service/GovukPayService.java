package uk.gov.pmrv.api.workflow.payment.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.restclient.RestClientApi;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.client.domain.CreatePaymentRequest;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentResponse;
import uk.gov.pmrv.api.workflow.payment.client.domain.enumeration.RestEndPointEnum;
import uk.gov.pmrv.api.workflow.payment.config.property.GovukPayProperties;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.pmrv.api.workflow.payment.transform.PaymentMapper;

import java.math.BigDecimal;

@Log4j2
@Component
@RequiredArgsConstructor
public class GovukPayService {

    private final RestTemplate restTemplate;
    private final GovukPayProperties govukPayProperties;

    private static final PaymentMapper PAYMENT_MAPPER = Mappers.getMapper(PaymentMapper.class);

    public static final int POUND_TO_PENCE_CONVERTER_FACTOR = 100;

    public PaymentCreateResult createPayment(PaymentCreateInfo paymentCreateInfo) {
        PaymentResponse paymentResponse = performCreatePaymentApiCall(paymentCreateInfo);

        if(paymentResponse == null) {
            throw new BusinessException(ErrorCode.PAYMENT_PROCESSING_FAILED);
        }

        return PAYMENT_MAPPER.toPaymentCreateResult(paymentResponse);
    }

    public PaymentGetResult getPayment(PaymentGetInfo paymentGetInfo) {
        PaymentResponse paymentResponse = performGetPaymentApiCall(paymentGetInfo);

        if(paymentResponse == null) {
            throw new BusinessException(ErrorCode.PAYMENT_PROCESSING_FAILED);
        }

        return PAYMENT_MAPPER.toPaymentGetResult(paymentResponse);
    }

    private PaymentResponse performCreatePaymentApiCall(PaymentCreateInfo paymentCreateInfo) {
    	RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(govukPayProperties.getServiceUrl())
                        .path(RestEndPointEnum.GOV_UK_CREATE_PAYMENT.getPath())
                        .build()
                        .toUri())
            .restEndPoint(RestEndPointEnum.GOV_UK_CREATE_PAYMENT)
            .headers(httpHeaders(paymentCreateInfo.getCompetentAuthority()))
            .body(CreatePaymentRequest.builder()
                .amount(paymentCreateInfo.getAmount().multiply(BigDecimal.valueOf(POUND_TO_PENCE_CONVERTER_FACTOR)).intValue())
                .reference(paymentCreateInfo.getPaymentRefNum())
                .description(paymentCreateInfo.getDescription())
                .returnUrl(paymentCreateInfo.getReturnUrl())
                .build())
            .restTemplate(restTemplate)
            .build();

        try{
            ResponseEntity<PaymentResponse> res = appRestApi.performApiCall();
            return res.getBody();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e.getMessage());
        }
    }

    private PaymentResponse performGetPaymentApiCall(PaymentGetInfo paymentGetInfo) {
    	RestClientApi appRestApi = RestClientApi.builder()
                .uri(UriComponentsBuilder
                        .fromUriString(govukPayProperties.getServiceUrl())
                        .path(RestEndPointEnum.GOV_UK_GET_PAYMENT.getPath())
                        .build(paymentGetInfo.getPaymentId()))
                .restEndPoint(RestEndPointEnum.GOV_UK_GET_PAYMENT)
                .headers(httpHeaders(paymentGetInfo.getCompetentAuthority()))
                .restTemplate(restTemplate)
                .build();

        try{
            ResponseEntity<PaymentResponse> res = appRestApi.performApiCall();
            return res.getBody();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e.getMessage());
        }
    }

    private HttpHeaders httpHeaders(CompetentAuthorityEnum competentAuthority) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(govukPayProperties.getApiKeys().get(competentAuthority.name().toLowerCase()));
        return httpHeaders;
    }
}
