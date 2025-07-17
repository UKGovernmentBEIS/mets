package uk.gov.pmrv.api.migration.emp.ukets.fummethods;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.fummethods.EmpFuelUseMonitoringMethods;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpFuelUseMonitoringMethodsSectionMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpFuelUseMonitoringMethods> {

	private final JdbcTemplate migrationJdbcTemplate;

	public EmpFuelUseMonitoringMethodsSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
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
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts\r\n"
    		+ "      from r1\r\n"
    		+ "     where fldMajorVersion = maxMajorVersion \r\n"
    		+ ")\r\n"
    		+ "select e.fldEmitterID, e.fldEmitterDisplayID,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details-Procedure_title                   )[1]', 'nvarchar(max)')), '') Procedure_details_Procedure_title                  ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details-Procedure_reference               )[1]', 'nvarchar(max)')), '') Procedure_details_Procedure_reference              ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details-Procedure_description             )[1]', 'nvarchar(max)')), '') Procedure_details_Procedure_description            ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details-Data_maintenance_post             )[1]', 'nvarchar(max)')), '') Procedure_details_Data_maintenance_post            ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details-Records_location                  )[1]', 'nvarchar(max)')), '') Procedure_details_Records_location                 ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details-System_name                       )[1]', 'nvarchar(max)')), '') Procedure_details_System_name                      ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Density_measurement_procedure-Procedure_title       )[1]', 'nvarchar(max)')), '') Density_measurement_procedure_Procedure_title      ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Density_measurement_procedure-Procedure_reference   )[1]', 'nvarchar(max)')), '') Density_measurement_procedure_Procedure_reference  ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Density_measurement_procedure-Procedure_description )[1]', 'nvarchar(max)')), '') Density_measurement_procedure_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Density_measurement_procedure-Data_maintenance_post )[1]', 'nvarchar(max)')), '') Density_measurement_procedure_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Density_measurement_procedure-Records_location      )[1]', 'nvarchar(max)')), '') Density_measurement_procedure_Records_location     ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Density_measurement_procedure-System_name           )[1]', 'nvarchar(max)')), '') Density_measurement_procedure_System_name          \r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'\r\n";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {

    	Map<String, String> zeroFuelUpliftMap = 
    			EmpFuelUseMonitoringMethodsMigrationMapper.getZeroFuelUpliftValuesForMigration();
        Map<String, EmpFuelUseMonitoringMethods> sections =
                queryEtsSection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                	EmissionsMonitoringPlanUkEts emp = emps.get(accountsToMigrate.get(etsAccId).getId()).getEmpContainer().getEmissionsMonitoringPlan();
                	Set<FuelConsumptionMeasuringMethod> methods = emp.getEmissionSources().getAircraftTypes()
                            .stream()
                            .map(AircraftTypeDetails::getFuelConsumptionMeasuringMethod)
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
                    	section.getFuelUpliftMethodProcedures().setZeroFuelUplift(zeroFuelUpliftMap.get(etsAccId));
                    	emp.setFuelUpliftMethodProcedures(section.getFuelUpliftMethodProcedures());
                    }
                });
    }

    @Override
    public Map<String, EmpFuelUseMonitoringMethods> queryEtsSection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsFuelUseMonitoringMethods> etsFuelUseMonitoringMethods = executeQuery(query, accountIds);

        return etsFuelUseMonitoringMethods.stream()
                .collect(Collectors.toMap(EtsFuelUseMonitoringMethods::getFldEmitterId,
                		EmpFuelUseMonitoringMethodsMigrationMapper::toEmpFuelUseMonitoringMethods));

    }

    private List<EtsFuelUseMonitoringMethods> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsFuelUseMonitoringMethodsRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
