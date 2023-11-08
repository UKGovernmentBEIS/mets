package uk.gov.pmrv.api.mireport.common.executedActions;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportParams;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#fromDate == null) || (#toDate == null) || T(java.time.LocalDate).parse(#fromDate).isBefore(T(java.time.LocalDate).parse(#toDate))}",
    message = "mireport.executedRequestActions.fromDate.toDate")
public class ExecutedRequestActionsMiReportParams extends MiReportParams {

    @NotNull(message = "{mireport.executedRequestActions.fromDate.notEmpty}")
    private LocalDate fromDate;

    private LocalDate toDate;
}
