package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationDreFeeDetails {

    @NotNull
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal totalBillableHours;

    @NotNull
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal hourlyRate;

    @NotNull
    @FutureOrPresent
    private LocalDate dueDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max=10000)
    private String comments;
}
