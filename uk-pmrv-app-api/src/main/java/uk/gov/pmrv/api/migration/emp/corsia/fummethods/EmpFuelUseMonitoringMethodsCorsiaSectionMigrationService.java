package uk.gov.pmrv.api.migration.emp.corsia.fummethods;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.fummethods.EmpFuelUseMonitoringMethods;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpFuelUseMonitoringMethodsCorsiaSectionMigrationService 
	implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpFuelUseMonitoringMethods> {

	private final JdbcTemplate migrationJdbcTemplate;

	public EmpFuelUseMonitoringMethodsCorsiaSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
		this.migrationJdbcTemplate = migrationJdbcTemplate;
	}

	private static final String QUERY_BASE = "with XMLNAMESPACES (\r\n"
    		+ "	'urn:www-toplev-com:officeformsofd' AS fd\r\n"
    		+ "), r1 as (\r\n"
    		+ "    select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,\r\n"
    		+ "           max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion\r\n"
    		+ "      from tblEmitter e\r\n"
    		+ "      join tblForm F             on f.fldEmitterID = e.fldEmitterID\r\n"
    		+ "      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\r\n"
    		+ "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n"
    		+ "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\r\n"
    		+ "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\r\n"
    		+ "), r2 as (\r\n"
    		+ "    select r1.*,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_corsia_yes)[1]', 'nvarchar(max)'), '') scopeCorsia\r\n"
    		+ "      from r1\r\n"
    		+ "     where fldMajorVersion = maxMajorVersion \r\n"
    		+ ")\r\n"
    		+ "select e.fldEmitterID,\r\n"
    		+ "       m.fldEmitterDisplayID,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_approach_justification)[1]', 'nvarchar(max)')), '') Corsia_approach_justification,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methoda-Procedure_title       )[1]', 'nvarchar(max)')), '') Procedure_details_methoda_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methoda-Procedure_reference   )[1]', 'nvarchar(max)')), '') Procedure_details_methoda_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methoda-Procedure_description )[1]', 'nvarchar(max)')), '') Procedure_details_methoda_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methoda-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Procedure_details_methoda_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methoda-Records_location      )[1]', 'nvarchar(max)')), '') Procedure_details_methoda_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methoda-System_name           )[1]', 'nvarchar(max)')), '') Procedure_details_methoda_System_name,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methoda-Procedure_title       )[1]', 'nvarchar(max)')), '') Fuel_density_methoda_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methoda-Procedure_reference   )[1]', 'nvarchar(max)')), '') Fuel_density_methoda_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methoda-Procedure_description )[1]', 'nvarchar(max)')), '') Fuel_density_methoda_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methoda-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Fuel_density_methoda_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methoda-Records_location      )[1]', 'nvarchar(max)')), '') Fuel_density_methoda_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methoda-System_name           )[1]', 'nvarchar(max)')), '') Fuel_density_methoda_System_name,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methodb-Procedure_title       )[1]', 'nvarchar(max)')), '') Procedure_details_methodb_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methodb-Procedure_reference   )[1]', 'nvarchar(max)')), '') Procedure_details_methodb_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methodb-Procedure_description )[1]', 'nvarchar(max)')), '') Procedure_details_methodb_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methodb-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Procedure_details_methodb_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methodb-Records_location      )[1]', 'nvarchar(max)')), '') Procedure_details_methodb_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_methodb-System_name           )[1]', 'nvarchar(max)')), '') Procedure_details_methodb_System_name,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methodb-Procedure_title       )[1]', 'nvarchar(max)')), '') Fuel_density_methodb_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methodb-Procedure_reference   )[1]', 'nvarchar(max)')), '') Fuel_density_methodb_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methodb-Procedure_description )[1]', 'nvarchar(max)')), '') Fuel_density_methodb_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methodb-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Fuel_density_methodb_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methodb-Records_location      )[1]', 'nvarchar(max)')), '') Fuel_density_methodb_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_methodb-System_name           )[1]', 'nvarchar(max)')), '') Fuel_density_methodb_System_name,\r\n"
    		+ "       \r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_fuel_uplift-Procedure_title       )[1]', 'nvarchar(max)')), '') Procedure_details_fuel_uplift_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_fuel_uplift-Procedure_reference   )[1]', 'nvarchar(max)')), '') Procedure_details_fuel_uplift_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_fuel_uplift-Procedure_description )[1]', 'nvarchar(max)')), '') Procedure_details_fuel_uplift_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_fuel_uplift-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Procedure_details_fuel_uplift_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_fuel_uplift-Records_location      )[1]', 'nvarchar(max)')), '') Procedure_details_fuel_uplift_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_fuel_uplift-System_name           )[1]', 'nvarchar(max)')), '') Procedure_details_fuel_uplift_System_name,\r\n"
    		+ "       \r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Zero_fuel_uplift_description)[1]', 'nvarchar(max)')), '') Zero_fuel_uplift_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_uplift                 )[1]', 'nvarchar(max)')), '') Fuel_uplift,\r\n"
    		+ "       \r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_fuel_uplift-Procedure_title       )[1]', 'nvarchar(max)')), '') Fuel_density_fuel_uplift_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_fuel_uplift-Procedure_reference   )[1]', 'nvarchar(max)')), '') Fuel_density_fuel_uplift_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_fuel_uplift-Procedure_description )[1]', 'nvarchar(max)')), '') Fuel_density_fuel_uplift_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_fuel_uplift-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Fuel_density_fuel_uplift_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_fuel_uplift-Records_location      )[1]', 'nvarchar(max)')), '') Fuel_density_fuel_uplift_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Fuel_density_fuel_uplift-System_name           )[1]', 'nvarchar(max)')), '') Fuel_density_fuel_uplift_System_name,\r\n"
    		+ "       \r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_block_off_block_on-Procedure_title       )[1]', 'nvarchar(max)')), '') Procedure_details_block_off_block_on_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_block_off_block_on-Procedure_reference   )[1]', 'nvarchar(max)')), '') Procedure_details_block_off_block_on_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_block_off_block_on-Procedure_description )[1]', 'nvarchar(max)')), '') Procedure_details_block_off_block_on_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_block_off_block_on-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Procedure_details_block_off_block_on_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_block_off_block_on-Records_location      )[1]', 'nvarchar(max)')), '') Procedure_details_block_off_block_on_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_block_off_block_on-System_name           )[1]', 'nvarchar(max)')), '') Procedure_details_block_off_block_on_System_name\r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter  ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "                             and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus   es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                             and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join      r2                on r2.fldEmitterID = e.fldEmitterID and scopeCorsia = 'true'\r\n"
    		+ "  left join mig_aviation_emitters m on m.fldEmitterID = e.fldEmitterID and m.scope = 'CORSIA'\r\n";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {

        Map<String, EmpFuelUseMonitoringMethods> sections =
                querySection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                	EmissionsMonitoringPlanCorsia emp = emps.get(accountsToMigrate.get(etsAccId).getId()).getEmpContainer().getEmissionsMonitoringPlan();
                	Set<FuelConsumptionMeasuringMethod> methods = emp.getEmissionSources().getAircraftTypes()
                            .stream()
                            .map(AircraftTypeDetailsCorsia::getFuelConsumptionMeasuringMethod)
                            .collect(Collectors.toSet());
                    if (methods.contains(FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF)) {
                    	emp.setBlockOnBlockOffMethodProcedures(section.getBlockOnBlockOffMethodProcedures());
                    }
                    if (methods.contains(FuelConsumptionMeasuringMethod.METHOD_A)) {
                    	emp.setMethodAProcedures(section.getMethodAProcedures());
                    }
                    if (methods.contains(FuelConsumptionMeasuringMethod.METHOD_B)) {
                    	emp.setMethodBProcedures(section.getMethodBProcedures());
                    }
                    if (methods.contains(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)) {
                    	emp.setFuelUpliftMethodProcedures(section.getFuelUpliftMethodProcedures());
                    }
                });
    }

    @Override
    public Map<String, EmpFuelUseMonitoringMethods> querySection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsFuelUseMonitoringMethodsCorsia> etsFuelUseMonitoringMethods = executeQuery(query, accountIds);

        return etsFuelUseMonitoringMethods.stream()
                .collect(Collectors.toMap(EtsFuelUseMonitoringMethodsCorsia::getFldEmitterID,
                		EmpFuelUseMonitoringMethodsCorsiaMigrationMapper::toEmpFuelUseMonitoringMethods));

    }

    private List<EtsFuelUseMonitoringMethodsCorsia> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsFuelUseMonitoringMethodsCorsiaRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
