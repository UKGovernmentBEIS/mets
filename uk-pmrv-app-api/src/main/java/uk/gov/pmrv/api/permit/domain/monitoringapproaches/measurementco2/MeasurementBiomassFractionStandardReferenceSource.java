package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

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
        message = "permit.measurementMonitoringApproach.tier.biomassFraction.standardReferenceSource.standardReferenceSourceType.otherDetails")
public class MeasurementBiomassFractionStandardReferenceSource extends MeasurementStandardReferenceSource {

    @NotNull
    private MeasurementBiomassFractionStandardReferenceSourceType type;
}
