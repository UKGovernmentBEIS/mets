package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitVariationRequestPaymentDetails {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    @FutureOrPresent
    private LocalDate dueDate;

    private String notes;

    @Builder.Default
    private Boolean manualPaymentInitiationPending = false;
}
