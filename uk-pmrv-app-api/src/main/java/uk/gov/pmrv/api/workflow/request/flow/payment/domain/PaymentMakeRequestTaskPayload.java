package uk.gov.pmrv.api.workflow.request.flow.payment.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.payment.domain.dto.BankAccountDetailsDTO;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentMakeRequestTaskPayload extends PaymentRequestTaskPayload {
    private Set<PaymentMethodType> paymentMethodTypes;
    private BankAccountDetailsDTO bankAccountDetails;
    private String externalPaymentId;
}
