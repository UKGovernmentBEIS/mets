package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certUsed) == (#certDetails != null)}", message = "aviationAer.corsia.monitoringApproach.certUsed")
public class AviationAerCorsiaMonitoringApproach {

    @NotNull
    private Boolean certUsed;

    @Valid
    private AviationAerCorsiaCertDetails certDetails;

    @Valid
    private AviationAerCorsiaFuelUseMonitoringDetails fuelUseMonitoringDetails;


}
