package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PaymentRequestActionPayload extends RequestActionPayload {
    private LocalDate paymentCreationDate;
    private String paymentRefNum;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethodType paymentMethod;
    private String paidByFullName;
    private LocalDate receivedDate;
    private LocalDate paymentDate;
}
