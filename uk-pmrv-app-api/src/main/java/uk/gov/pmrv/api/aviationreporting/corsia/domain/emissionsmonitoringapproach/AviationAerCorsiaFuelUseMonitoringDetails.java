package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#fuelDensityType eq 'ACTUAL_DENSITY' or #fuelDensityType eq 'ACTUAL_STANDARD_DENSITY' or #fuelDensityType eq 'STANDARD_DENSITY') == (#identicalToProcedure != null)}",
        message = "aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.identicalToProcedure")
@SpELExpression(expression = "{(#fuelDensityType eq 'ACTUAL_DENSITY' or #fuelDensityType eq 'ACTUAL_STANDARD_DENSITY' or #fuelDensityType eq 'STANDARD_DENSITY') == (#blockHourUsed != null)}",
        message = "aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.blockHourUsed")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#blockHourUsed) == (#aircraftTypeDetails?.size() gt 0)}", message = "aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.aircraftTypeDetails")

public class AviationAerCorsiaFuelUseMonitoringDetails {

    @NotNull
    private AviationAerCorsiaFuelDensityType fuelDensityType;

    private Boolean identicalToProcedure;

    private Boolean blockHourUsed;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull @Valid AviationAerCorsiaAircraftTypeDetails> aircraftTypeDetails = new HashSet<>();

}
