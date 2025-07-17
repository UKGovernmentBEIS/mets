package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'OTHER') == (#otherTypeName != null)}", 
    message = "permit.transferredCO2MonitoringApproach.measurementDevice.otherTypeName")
public class MeasurementDevice {

    @Size(max = 10000)
    @NotBlank
    private String reference;

    @NotNull
    private MeasurementDeviceType type;

    @Size(max = 10000)
    private String otherTypeName;

    @Size(max = 10000)
    @NotBlank
    private String location;

}
