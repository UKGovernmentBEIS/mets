package uk.gov.pmrv.api.permit.domain.regulatedactivities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitIdSection;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(
        expression = "((#type == 'UPSTREAM_GHG_REMOVAL' and #capacity == null and #capacityUnit == null) " +
                "or (#type != 'UPSTREAM_GHG_REMOVAL' and #capacity != null and #capacity > 0 and #capacityUnit != null))",
        message = "permit.regulatedActivities.invalid_input"
)
public class RegulatedActivity extends PermitIdSection {

    @NotNull
    private RegulatedActivityType type;

    private BigDecimal capacity;

    private CapacityUnit capacityUnit;
}
