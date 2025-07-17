package uk.gov.pmrv.api.migration.emp.ukets.emissionsources.emissionsourcesdetails;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsources.EmpEmissionSourcesMigrationMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpEmissionSourcesDetailsMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpEmissionSources> {

	private final JdbcTemplate migrationJdbcTemplate;

    public EmpEmissionSourcesDetailsMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
    }

    private static final String QUERY_BASE  =
        "with XMLNAMESPACES (\r\n"
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
        + "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Approach_justification                                     )[1]', 'nvarchar(max)')), '') Approach_justification,\r\n"
        + "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_additional_aircraft-Procedure_title      )[1]', 'nvarchar(max)')), '') Procedure_details_additional_aircraft_Procedure_title      ,\r\n"
        + "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_additional_aircraft-Procedure_reference  )[1]', 'nvarchar(max)')), '') Procedure_details_additional_aircraft_Procedure_reference  ,\r\n"
        + "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_additional_aircraft-Procedure_description)[1]', 'nvarchar(max)')), '') Procedure_details_additional_aircraft_Procedure_description,\r\n"
        + "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_additional_aircraft-Data_maintenance_post)[1]', 'nvarchar(max)')), '') Procedure_details_additional_aircraft_Data_maintenance_post,\r\n"
        + "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_additional_aircraft-Records_location     )[1]', 'nvarchar(max)')), '') Procedure_details_additional_aircraft_Records_location     ,\r\n"
        + "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_additional_aircraft-System_name          )[1]', 'nvarchar(max)')), '') Procedure_details_additional_aircraft_System_name          \r\n"
        + "  from tblEmitter e\r\n"
        + "  join tblAviationEmitter  ae on ae.fldEmitterID = e.fldEmitterID\r\n"
        + "                             and ae.fldCommissionListName = 'UK ETS'\r\n"
        + "  join tlkpEmitterStatus   es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
        + "                             and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
        + "  join      r2                on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'\r\n"
        ;
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, 
    		Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {
        Map<String, EmpEmissionSources> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigrate.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                emps.get(accountsToMigrate.get(etsAccId).getId())
                    .getEmpContainer().getEmissionsMonitoringPlan().setEmissionSources(section)); 
        
    }

    @Override
    public Map<String, EmpEmissionSources> queryEtsSection(List<String> accountIds) {
        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpEmissionSources> empEmissionSources = executeQuery(query, accountIds);

        return empEmissionSources.stream()
                .collect(Collectors.toMap(EtsEmpEmissionSources::getFldEmitterId,
                		EmpEmissionSourcesMigrationMapper::toEmpEmissionSources));
    }

    private List<EtsEmpEmissionSources> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmpEmissionSourcesRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
