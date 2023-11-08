package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentConfirmRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentStatus;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfirmPaymentInitializerTest {

    private final ConfirmPaymentInitializer confirmPaymentInitializer = new ConfirmPaymentInitializer();

    @Test
    void initializePayload() {
        BigDecimal amount = BigDecimal.valueOf(20320.52);
        String requestId = "request-id-1";
        LocalDate paymentCreationDate = LocalDate.now().minusDays(2);
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
                .paymentDate(LocalDate.now().minusDays(4))
                .paidByFullName("user")
                .amount(amount)
                .status(PaymentStatus.MARK_AS_PAID)
                .paymentMethod(PaymentMethodType.BANK_TRANSFER)
                .paymentRefNum(requestId)
                .paymentCreationDate(paymentCreationDate)
                .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .paymentAmount(amount)
                .requestPaymentInfo(requestPaymentInfo)
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        RequestTaskPayload requestTaskPayload = confirmPaymentInitializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PAYMENT_CONFIRM_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PaymentConfirmRequestTaskPayload.class);

        PaymentConfirmRequestTaskPayload paymentConfirmRequestTaskPayload = (PaymentConfirmRequestTaskPayload) requestTaskPayload;

        assertEquals(request.getId(), paymentConfirmRequestTaskPayload.getPaymentRefNum());
        assertEquals(requestPaymentInfo.getPaymentDate(), paymentConfirmRequestTaskPayload.getPaymentDate());
        assertEquals(requestPaymentInfo.getPaidByFullName(), paymentConfirmRequestTaskPayload.getPaidByFullName());
        assertEquals(requestPaymentInfo.getAmount(), paymentConfirmRequestTaskPayload.getAmount());
        assertEquals(requestPaymentInfo.getStatus(), paymentConfirmRequestTaskPayload.getStatus());
        assertEquals(requestPaymentInfo.getPaymentMethod(), paymentConfirmRequestTaskPayload.getPaymentMethod());
        assertEquals(requestPaymentInfo.getPaymentRefNum(), paymentConfirmRequestTaskPayload.getPaymentRefNum());
        assertEquals(requestPaymentInfo.getPaymentCreationDate(), paymentConfirmRequestTaskPayload.getCreationDate());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(confirmPaymentInitializer.getRequestTaskTypes()).containsExactlyElementsOf(RequestTaskType.getConfirmPaymentTypes());
    }

}