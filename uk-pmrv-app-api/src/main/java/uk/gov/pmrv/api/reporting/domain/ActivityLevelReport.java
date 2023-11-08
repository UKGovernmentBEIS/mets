package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#freeAllocationOfAllowances == (#file != null)}", message = "aer.activityLevelReport.freeAllocationOfAllowances")
public class ActivityLevelReport {

    private boolean freeAllocationOfAllowances;

    private UUID file;
}
