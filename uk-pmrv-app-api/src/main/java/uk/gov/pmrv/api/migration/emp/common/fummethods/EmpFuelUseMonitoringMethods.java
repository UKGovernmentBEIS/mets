package uk.gov.pmrv.api.migration.emp.common.fummethods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpFuelUseMonitoringMethods implements EmpUkEtsSection, EmpCorsiaSection {

	private EmpMethodAProcedures methodAProcedures;

    private EmpMethodBProcedures methodBProcedures;

    private EmpBlockOnBlockOffMethodProcedures blockOnBlockOffMethodProcedures;

    private EmpFuelUpliftMethodProcedures fuelUpliftMethodProcedures;
}
