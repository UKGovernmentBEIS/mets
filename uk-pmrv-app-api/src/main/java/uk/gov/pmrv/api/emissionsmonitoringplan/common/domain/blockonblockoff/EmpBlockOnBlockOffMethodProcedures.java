package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpBlockOnBlockOffMethodProcedures implements EmpUkEtsSection, EmpCorsiaSection {

    @NotNull
    @Valid
    private EmpProcedureForm fuelConsumptionPerFlight;
}
