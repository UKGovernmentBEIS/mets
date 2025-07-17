package uk.gov.pmrv.api.reporting.domain.regulatedactivities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#hasEnergyCrf) || T(java.lang.Boolean).TRUE.equals(#hasIndustrialCrf)}", message = "aer.regulated.activities.at.least.one.crf")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#hasEnergyCrf) == (#energyCrf != null)}", message = "aer.regulated.activities.empty.energy.crf")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#hasIndustrialCrf) == (#industrialCrf != null)}", message = "aer.regulated.activities.empty.industrial.crf")
public class AerRegulatedActivity extends RegulatedActivity {

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean hasEnergyCrf;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean hasIndustrialCrf;

    private CrfCode energyCrf;

    private CrfCode industrialCrf;
}
