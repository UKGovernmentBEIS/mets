package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SpELExpression(expression = "{(#years?.size() gt 0) && (#years.?[(#this ge 2021 && #this le 2030)]?.size() gt 0)}",
    message = "returnOfAllowances.year")
public class ReturnOfAllowances {

    @NotNull
    private Integer numberOfAllowancesToBeReturned;

    @NotNull
    private List<Integer> years;

    @NotEmpty
    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;

    @Future
    @NotNull
    LocalDate dateToBeReturned;

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String regulatorComments;
}
