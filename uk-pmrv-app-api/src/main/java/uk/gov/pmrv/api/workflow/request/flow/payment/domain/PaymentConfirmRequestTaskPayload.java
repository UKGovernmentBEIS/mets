package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentConfirmRequestTaskPayload extends PaymentRequestTaskPayload {
    private LocalDate paymentDate;
    private String paidByFullName;
    private PaymentStatus status;
    private PaymentMethodType paymentMethod;
}
