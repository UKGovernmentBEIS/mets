package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#followed and #notFollowingHierarchicalOrderReason == null and (#notFollowingHierarchicalOrderDescription == null or #notFollowingHierarchicalOrderDescription.trim().length() == 0)) or " +
                "(#followed == false and #notFollowingHierarchicalOrderReason != null and #notFollowingHierarchicalOrderDescription != null and #notFollowingHierarchicalOrderDescription.trim().length() > 0)}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.invalid_hierarchicalOrder"
)
public class SubInstallationHierarchicalOrder {

    @NotNull
    private boolean followed;

    private NotFollowingHierarchicalOrderReason notFollowingHierarchicalOrderReason;

    @Size(max = 10000)
    private String notFollowingHierarchicalOrderDescription;
}
