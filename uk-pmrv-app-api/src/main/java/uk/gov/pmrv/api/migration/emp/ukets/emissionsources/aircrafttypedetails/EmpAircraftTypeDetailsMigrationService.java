package uk.gov.pmrv.api.migration.emp.ukets.emissionsources.aircrafttypedetails;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsources.EmpEmissionSourcesMigrationMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpAircraftTypeDetailsMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpEmissionSources> {

	private final JdbcTemplate migrationJdbcTemplate;

    public EmpAircraftTypeDetailsMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
    }

    private static final String AIRCRAFT_USED_QUERY  =
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
        + "), at1 as (\r\n"
        + "    select fldEmitterID,\r\n"
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
        + "           nullif(trim(m.c.query('Methodology             ').value('.', 'nvarchar(max)')), '') Methodology\r\n"
        + "      from r2\r\n"
        + "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Aircraft_types-Ae_plan_aircraft_types/row)') as t(c)\r\n"
        + "     outer apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Fuel_consumption_methodology/row)') as m(c)\r\n"
        + "), at2 as (\r\n"
        + "    select * from at1\r\n"
        + "     where Generic_aircraft_type = isnull(mGeneric_aircraft_type, Generic_aircraft_type)\r\n"
        + ")\r\n"
        + "select e.fldEmitterID, e.fldEmitterDisplayID,\r\n"
        + "       Generic_aircraft_type, Hmake, Hmodel, Hdesignator, Generic_aircraft_subtype,\r\n"
        + "       Number_of_aircraft, Chk_jet_kerosene, Chk_jet_gasoline, Chk_aviation_gasoline,\r\n"
        + "       Methodology\r\n"
        + "  from tblEmitter e\r\n"
        + "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
        + "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
        + "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
        + "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
        + "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'\r\n"
        + "  join at2 a                 on a.fldEmitterID  = e.fldEmitterID\r\n"
        ;
    
    private static final String ADDITIONAL_AIRCRAFT_QUERY  =
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
            + "), at1 as (\r\n"
            + "    select fldEmitterID,\r\n"
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
            + "           nullif(trim(m.c.query('Methodology             ').value('.', 'nvarchar(max)')), '') Methodology\r\n"
            + "      from r2\r\n"
            + "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Additional_aircraft_types-Ae_plan_aircraft_types/row)') as t(c)\r\n"
            + "     outer apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Fuel_consumption_methodology/row)') as m(c)\r\n"
            + "), at2 as (\r\n"
            + "    select * from at1\r\n"
            + "     where Generic_aircraft_type = isnull(mGeneric_aircraft_type, Generic_aircraft_type)\r\n"
            + ")\r\n"
            + "select e.fldEmitterID, e.fldEmitterDisplayID,\r\n"
            + "       Generic_aircraft_type, Hmake, Hmodel, Hdesignator, Generic_aircraft_subtype,\r\n"
            + "       Number_of_aircraft, Chk_jet_kerosene, Chk_jet_gasoline, Chk_aviation_gasoline,\r\n"
            + "       Methodology\r\n"
            + "  from tblEmitter e\r\n"
            + "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
            + "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
            + "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
            + "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
            + "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'\r\n"
            + "  join at2 a                 on a.fldEmitterID  = e.fldEmitterID\r\n"
            ;
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, 
    		Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {
    	List<String> accountIds = new ArrayList<>(accountsToMigrate.keySet());
        
        String aircraftInUseQuery = constructSectionQuery(AIRCRAFT_USED_QUERY, accountIds);
        Map<String, Set<AircraftTypeDetails>> aircraftInUseSections = executeQuery(aircraftInUseQuery, accountIds)
        		.stream()
                .collect(Collectors.groupingBy(EtsAircraftTypeDetails::getFldEmitterId))
                .entrySet()
              .stream()
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> EmpEmissionSourcesMigrationMapper.toAircraftTypeDetails(entry.getValue())));
        String additionalAircraftQuery = constructSectionQuery(ADDITIONAL_AIRCRAFT_QUERY, accountIds);
        Map<String, Set<AircraftTypeDetails>> additionalAircraftSections = executeQuery(additionalAircraftQuery, accountIds)
        		.stream()
                .collect(Collectors.groupingBy(EtsAircraftTypeDetails::getFldEmitterId))
                .entrySet()
  			.stream()
  			.collect(Collectors.toMap(Map.Entry::getKey, entry -> EmpEmissionSourcesMigrationMapper.toAdditionalAircraftTypeDetails(entry.getValue())));
        // combine aircraft in use and additional aircraft into a single map
        Map<String, Set<AircraftTypeDetails>> allAircraftSections = Stream.of(aircraftInUseSections, additionalAircraftSections)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            v1.addAll(v2);
                            return v1;
                        }));
        
        allAircraftSections
            .forEach((etsAccId, allAircraftSection) -> 
                emps.get(accountsToMigrate.get(etsAccId).getId())
                    .getEmpContainer().getEmissionsMonitoringPlan().getEmissionSources().setAircraftTypes(allAircraftSection));
        
    }
    
    @Override
    public Map<String, EmpEmissionSources> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }


	private List<EtsAircraftTypeDetails> executeQuery(String query, List<String> accountIds) {
    	return migrationJdbcTemplate.query(query,
                new EtsAircraftTypeDetailsRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    			
    }
	
}
