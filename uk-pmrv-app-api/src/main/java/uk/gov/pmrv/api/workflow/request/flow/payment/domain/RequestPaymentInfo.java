package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPaymentInfo {
    private LocalDate paymentCreationDate;
    private LocalDate paymentDate;
    private String paidById;
    private String paidByFullName;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethodType paymentMethod;
    private String paymentRefNum;

    private LocalDate receivedDate;
    private String cancellationReason;
}
