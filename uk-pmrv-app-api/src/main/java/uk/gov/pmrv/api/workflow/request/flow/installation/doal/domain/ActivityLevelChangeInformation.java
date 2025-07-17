package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#areConservativeEstimates) == (#explainEstimates != null)}", message = "doal.activityLevelChangeInformation.areConservativeEstimates")
public class ActivityLevelChangeInformation {

    @Builder.Default
    private List<@Valid @NotNull ActivityLevel> activityLevels = new ArrayList<>();

    private Boolean areConservativeEstimates;

    @Size(max = 10000)
    private String explainEstimates;

    @Builder.Default
    private SortedSet<@Valid @NotNull PreliminaryAllocation> preliminaryAllocations = new TreeSet<>();

    @Size(max = 10000)
    private String commentsForUkEtsAuthority;
}
