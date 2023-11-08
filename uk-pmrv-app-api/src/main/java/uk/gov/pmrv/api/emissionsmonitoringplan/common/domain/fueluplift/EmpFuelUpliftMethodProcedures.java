package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelUpliftSupplierRecordType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpFuelUpliftMethodProcedures implements EmpUkEtsSection, EmpCorsiaSection {

    @NotNull
    @Valid
    private EmpProcedureForm blockHoursPerFlight;

    @NotBlank
    @Size(max = 2000)
    private String zeroFuelUplift;

    @NotNull
    private FuelUpliftSupplierRecordType fuelUpliftSupplierRecordType;

    @NotNull
    @Valid
    private EmpProcedureForm fuelDensity;
}
