package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.flightaircraftprocedures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpFlightAndAircraftProcedures implements EmpUkEtsSection {

    @NotNull
    @Valid
    private EmpProcedureForm aircraftUsedDetails;

    @NotNull
    @Valid
    private EmpProcedureForm flightListCompletenessDetails;

    @NotNull
    @Valid
    private EmpProcedureForm ukEtsFlightsCoveredDetails;
}
