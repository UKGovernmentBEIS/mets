package uk.gov.pmrv.api.migration.emp.corsia.emissionsources;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpEmissionSourcesCorsiaSectionMigrationService implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpEmissionSourcesCorsia> {

	private final JdbcTemplate migrationJdbcTemplate;

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
    		+ "), at1 as (\r\n"
    		+ "    select fldEmitterID, scopeCorsia,\r\n"
    		+ "           nullif(trim(t.c.query('Generic_aircraft_type   ').value('.', 'nvarchar(max)')), '') Generic_aircraft_type   ,\r\n"
    		+ "           nullif(trim(t.c.query('Hmake                   ').value('.', 'nvarchar(max)')), '') Hmake                   ,\r\n"
    		+ "           nullif(trim(t.c.query('Hmodel                  ').value('.', 'nvarchar(max)')), '') Hmodel                  ,\r\n"
    		+ "           nullif(trim(t.c.query('Hdesignator             ').value('.', 'nvarchar(max)')), '') Hdesignator             ,\r\n"
    		+ "           nullif(trim(t.c.query('Generic_aircraft_subtype').value('.', 'nvarchar(max)')), '') Generic_aircraft_subtype,\r\n"
    		+ "           nullif(trim(t.c.query('Number_of_aircraft      ').value('.', 'nvarchar(max)')), '') Number_of_aircraft      ,\r\n"
    		+ "           nullif(trim(t.c.query('Chk_jet_kerosene        ').value('.', 'nvarchar(max)')), '') Chk_jet_kerosene        ,\r\n"
    		+ "           nullif(trim(t.c.query('Chk_jet_gasoline        ').value('.', 'nvarchar(max)')), '') Chk_jet_gasoline        ,\r\n"
    		+ "           nullif(trim(t.c.query('Chk_aviation_gasoline   ').value('.', 'nvarchar(max)')), '') Chk_aviation_gasoline   ,\r\n"
    		+ "           nullif(trim(m.c.query('Generic_aircraft_type   ').value('.', 'nvarchar(max)')), '') mGeneric_aircraft_type  ,\r\n"
    		+ "           nullif(trim(m.c.query('Corsia_methodology      ').value('.', 'nvarchar(max)')), '') Corsia_methodology\r\n"
    		+ "      from r2\r\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Corsia_aircraft_types-Corsia_ae_plan_aircraft_types/row)') as t(c)\r\n"
    		+ "     outer apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Corsia_fuel_methodology/row)') as m(c)\r\n"
    		+ "), at2 as (\r\n"
    		+ "    select * from at1\r\n"
    		+ "     where Generic_aircraft_type = isnull(mGeneric_aircraft_type, Generic_aircraft_type)\r\n"
    		+ ")\r\n"
    		+ "select e.fldEmitterID,\r\n"
    		+ "       m.fldEmitterDisplayID,\r\n"
    		+ "       Generic_aircraft_type, Hmake, Hmodel, Hdesignator, Generic_aircraft_subtype,\r\n"
    		+ "       Number_of_aircraft, Chk_jet_kerosene, Chk_jet_gasoline, Chk_aviation_gasoline,\r\n"
    		+ "       Corsia_methodology\r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeCorsia = 'true'\r\n"
    		+ "  join at2 a                 on a.fldEmitterID  = e.fldEmitterID\r\n"
    		+ "  left join mig_aviation_emitters m on m.fldEmitterID = e.fldEmitterID and m.scope = 'CORSIA'\r\n";
    
	@Override
	public void populateSection(Map<String, Account> accountsToMigrate,
			Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {
		Map<String, EmpEmissionSourcesCorsia> sections =
                querySection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setEmissionSources(section);
                });
		
	}

    @Override
    public Map<String, EmpEmissionSourcesCorsia> querySection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmissionSourcesCorsia> emissionSources = executeQuery(query, accountIds);

        return emissionSources.stream()
                .collect(Collectors.groupingBy(EtsEmissionSourcesCorsia::getFldEmitterID))
                .entrySet()
              .stream()
              .collect(Collectors.toMap(
            		  Map.Entry::getKey, entry -> EmpEmissionSourcesCorsiaMigrationMapper.toEmissionSources(entry.getValue())));

    }

    private List<EtsEmissionSourcesCorsia> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmissionSourcesCorsiaRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
