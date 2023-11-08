package uk.gov.pmrv.api.workflow.request.flow.common.vir.domain;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "#improvementCompleted == (#dateCompleted != null)", message = "operatorImprovementFollowUpResponse.dateCompleted")
@SpELExpression(expression = "!(#improvementCompleted) == (#reason != null)", message = "operatorImprovementFollowUpResponse.reason")
public class OperatorImprovementFollowUpResponse {

    private boolean improvementCompleted;

    private LocalDate dateCompleted;

    @Size(max = 10000)
    private String reason;
}
