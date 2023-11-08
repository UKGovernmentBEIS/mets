package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class DreFeeDetails {
	
	@NotNull
    @Positive
	private BigDecimal totalBillableHours;
	
	@NotNull
    @Positive
	private BigDecimal hourlyRate;
	
	@NotNull
    @FutureOrPresent
    private LocalDate dueDate;
	
}
