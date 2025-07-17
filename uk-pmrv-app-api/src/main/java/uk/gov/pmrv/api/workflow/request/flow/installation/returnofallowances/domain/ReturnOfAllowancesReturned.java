package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(T(java.lang.Boolean).TRUE.equals(#isAllowancesReturned) && #returnedAllowancesDate != null) " +
    "|| (T(java.lang.Boolean).FALSE.equals(#isAllowancesReturned) && #returnedAllowancesDate == null)}",
    message = "returnOfAllowancesReturned.year")
public class ReturnOfAllowancesReturned {

    @NotNull
    private Boolean isAllowancesReturned;

    @PastOrPresent
    private LocalDate returnedAllowancesDate;

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String regulatorComments;

}
