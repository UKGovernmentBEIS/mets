package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpOperatingStatePairsCorsiaDetails {

    @NotBlank
    private String stateA;

    @NotBlank
    private String stateB;
}