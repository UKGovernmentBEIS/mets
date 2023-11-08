package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SpELExpression(expression = "{(#reasonType == 'OTHER') == (#otherReason != null)}",
    message = "withholdingOfAllowances.otherReason")
public class WithholdingOfAllowances {

    @Range(min = 2023, max = 2030)
    private int year;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private WithholdingOfAllowancesReasonType reasonType;

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String otherReason;

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String regulatorComments;
}
