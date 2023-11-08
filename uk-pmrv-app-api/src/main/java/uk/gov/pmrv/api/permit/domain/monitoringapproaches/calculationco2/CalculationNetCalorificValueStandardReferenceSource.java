package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import jakarta.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'OTHER') == (#otherTypeDetails != null)}",
        message = "permit.calculationApproach.tier.netCalorificValue.standardReferenceSource.standardReferenceSourceType.otherDetails")
public class CalculationNetCalorificValueStandardReferenceSource extends CalculationStandardReferenceSource {

    @NotNull
    private CalculationNetCalorificValueStandardReferenceSourceType type;
}
