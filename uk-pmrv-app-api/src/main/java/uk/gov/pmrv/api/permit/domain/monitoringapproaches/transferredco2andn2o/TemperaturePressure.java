package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#measurementDevices?.size() gt 0)}", 
    message = "permit.transferredCO2MonitoringApproach.temperaturePressure.exist")
public class TemperaturePressure {

    private boolean exist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MeasurementDevice> measurementDevices;

}
