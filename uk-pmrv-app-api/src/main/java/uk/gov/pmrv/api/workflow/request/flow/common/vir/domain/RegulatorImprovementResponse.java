package uk.gov.pmrv.api.workflow.request.flow.common.vir.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "#improvementRequired == (#improvementDeadline != null)", message = "regulatorImprovementResponse.improvementRequired")
public class RegulatorImprovementResponse {

    private boolean improvementRequired;

    private LocalDate improvementDeadline;

    @Size(max = 10000)
    private String improvementComments;

    @NotNull
    @Size(max = 10000)
    private String operatorActions;
}
