package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class MeasurementStandardReferenceSource {
    
    @Size(max = 500)
    private String otherTypeDetails;
    
    @Size(max = 500)
    private String defaultValue;
}
