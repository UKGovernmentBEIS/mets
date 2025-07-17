package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(expression = "{(#type eq 'OTHER_DATASOURCE') == (#otherDataSourceExplanation != null)}",
    message = "aviationdDre.emissionsCalculationApproach.otherDataSourceExplanation")
public class AviationDreEmissionsCalculationApproach {

    @NotNull
    private AviationDreEmissionsCalculationApproachType type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max=10000)
    private String otherDataSourceExplanation;
}
