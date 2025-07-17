package uk.gov.pmrv.api.workflow.request.flow.payment.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentConfirmRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PaymentPayloadMapper {


    PaymentProcessedRequestActionPayload toPaymentProcessedRequestActionPayload(RequestPaymentInfo requestPaymentInfo, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PAYMENT_CANCELLED_PAYLOAD)")
    PaymentCancelledRequestActionPayload toPaymentCancelledRequestActionPayload(RequestPaymentInfo requestPaymentInfo);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PAYMENT_CONFIRM_PAYLOAD)")
    @Mapping(target = "creationDate", source = "requestPaymentInfo.paymentCreationDate")
    PaymentConfirmRequestTaskPayload toConfirmPaymentRequestTaskPayload(RequestPaymentInfo requestPaymentInfo);
}
