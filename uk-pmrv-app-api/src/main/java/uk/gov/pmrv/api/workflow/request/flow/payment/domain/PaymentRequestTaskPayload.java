package uk.gov.pmrv.api.workflow.request.flow.payment.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PaymentRequestTaskPayload extends RequestTaskPayload {
    private BigDecimal amount;
    private String paymentRefNum;
    private LocalDate creationDate;
}
