package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementLaboratory {
    
    @Size(max = 250)
    @NotBlank
    private String laboratoryName;

    @NotNull
    private Boolean laboratoryAccredited;
}
