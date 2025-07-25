package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'OTHER') == (#otherTypeDetails?.length() gt 0)}",
        message = "permit.calculationApproach.tier.biomassFraction.standardReferenceSource.standardReferenceSourceType.otherDetails")
public class CalculationBiomassFractionStandardReferenceSource extends CalculationStandardReferenceSource {

    @NotNull
    private CalculationBiomassFractionStandardReferenceSourceType type;
}
