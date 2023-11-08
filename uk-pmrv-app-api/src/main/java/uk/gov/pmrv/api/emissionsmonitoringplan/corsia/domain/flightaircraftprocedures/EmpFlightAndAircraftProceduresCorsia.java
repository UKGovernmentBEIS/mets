package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpFlightAndAircraftProceduresCorsia implements EmpCorsiaSection {

    @NotNull
    @Valid
    private EmpProcedureForm aircraftUsedDetails;

    @NotNull
    @Valid
    private EmpProcedureForm flightListCompletenessDetails;

    @NotNull
    @Valid
    private EmpProcedureForm internationalFlightsDetermination;

    @NotNull
    @Valid
    private EmpOperatingStatePairsCorsia operatingStatePairs;

    @NotNull
    @Valid
    private EmpProcedureForm internationalFlightsDeterminationOffset;

    @NotNull
    @Valid
    private EmpProcedureForm internationalFlightsDeterminationNoMonitoring;
}
