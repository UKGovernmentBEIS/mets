package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionsMonitoringPlanValidationResult {

    private boolean valid;

    @Builder.Default
    private List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();

    public static EmissionsMonitoringPlanValidationResult validEmissionsMonitoringPlan() {
        return EmissionsMonitoringPlanValidationResult.builder().valid(true).build();
    }

    public static EmissionsMonitoringPlanValidationResult invalidEmissionsMonitoringPlan(List<EmissionsMonitoringPlanViolation> empViolations) {
        return EmissionsMonitoringPlanValidationResult.builder().valid(false).empViolations(empViolations).build();
    }
}
