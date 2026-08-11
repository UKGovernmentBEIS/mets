package uk.gov.pmrv.api.workflow.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFee {

    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "changeable", nullable = false)
    private boolean changeable = true;

    @Column(name = "scheduled_amount")
    private BigDecimal scheduledAmount;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;
}
