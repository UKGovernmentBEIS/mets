package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitCessation {

    @NotNull
    private PermitCessationDeterminationOutcome determinationOutcome;

    @PastOrPresent
    private LocalDate allowancesSurrenderDate;

    @Min(value = 0)
    @Max(value = 99999999)
    private Integer numberOfSurrenderAllowances;

    @NotNull
    @DecimalMin(value = "0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal annualReportableEmissions;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean subsistenceFeeRefunded;

    @NotNull
    private PermitCessationOfficialNoticeType noticeType;

    @NotBlank
    @Size(max = 10000)
    private String notes;
}
