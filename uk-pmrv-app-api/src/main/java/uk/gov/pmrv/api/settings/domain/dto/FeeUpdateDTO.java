package uk.gov.pmrv.api.settings.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeUpdateDTO {

    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "New payment amount must be more than 0")
    private BigDecimal amount;

    @NotNull
    private LocalDate effectiveDate;
}
