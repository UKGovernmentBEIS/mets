package uk.gov.pmrv.api.migration.emp.corsia.abbreviations;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.abbreviations.EmpAbbreviationsMigrationMapper;
import uk.gov.pmrv.api.migration.emp.common.abbreviations.EtsEmpAbbreviation;
import uk.gov.pmrv.api.migration.emp.common.abbreviations.EtsEmpAbbreviationRowMapper;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpAbbreviationsCorsiaSectionMigrationService 
	implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpAbbreviations>{

	private final JdbcTemplate migrationJdbcTemplate;
	private final EmpAbbreviationsMigrationMapper empAbbreviationsMigrationMapper;

    private static final String QUERY_BASE  = "with XMLNAMESPACES (\r\n"
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
    		+ "), a as (\r\n"
    		+ "    select fldEmitterID,\r\n"
    		+ "           nullif(trim(t.c.query('Abbreviation').value('.', 'nvarchar(max)')), '') Abbreviation,\r\n"
    		+ "           nullif(trim(t.c.query('Definition  ').value('.', 'nvarchar(max)')), '') Definition\r\n"
    		+ "      from r2\r\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Abbreviations-Abbreviation/row)') as t(c)\r\n"
    		+ ")\r\n"
    		+ "select e.fldEmitterID, m.fldEmitterDisplayID, Abbreviation, Definition\r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeCorsia = 'true'\r\n"
    		+ "  join a                     on a.fldEmitterID  = e.fldEmitterID\r\n"
    		+ "  left join mig_aviation_emitters m on m.fldEmitterID = e.fldEmitterID and m.scope = 'CORSIA'\r\n";

	@Override
	public void populateSection(Map<String, Account> accountsToMigrate,
			Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {
		Map<String, EmpAbbreviations> sections = 
                querySection(new ArrayList<>(accountsToMigrate.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                emps.get(accountsToMigrate.get(etsAccId).getId())
                    .getEmpContainer().getEmissionsMonitoringPlan().setAbbreviations(section)); 	
	}

    @Override
    public Map<String, EmpAbbreviations> querySection(List<String> accountIds) {
        String query = constructSectionQuery(QUERY_BASE, accountIds);

        Map<String, List<EtsEmpAbbreviation>> etsEmpAbbreviations = executeQuery(query, accountIds);

        return etsEmpAbbreviations.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> toEmpAbbreviations(entry.getValue())));
    }

    private Map<String, List<EtsEmpAbbreviation>> executeQuery(String query, List<String> accountIds) {
        List<EtsEmpAbbreviation> etsEmpAbbreviations = migrationJdbcTemplate.query(
                query,
                new EtsEmpAbbreviationRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsEmpAbbreviations
            .stream()
            .collect(Collectors.groupingBy(EtsEmpAbbreviation::getFldEmitterId));
    }

    private EmpAbbreviations toEmpAbbreviations(List<EtsEmpAbbreviation> etsEmpAbbreviations) {
    	EmpAbbreviations empAbbreviations = EmpAbbreviations.builder()
            .exist(false)
            .build();

        if(existEmpAbbreviations(etsEmpAbbreviations)) {
        	empAbbreviations.setExist(true);
        	empAbbreviations.setAbbreviationDefinitions(empAbbreviationsMigrationMapper.toEmpAbbreviationDefinitions(etsEmpAbbreviations));
        }

        return empAbbreviations;
    }

    private boolean existEmpAbbreviations(List<EtsEmpAbbreviation> etsEmpAbbreviations) {
        if (etsEmpAbbreviations.isEmpty()) {
            return false;
        }

        if(etsEmpAbbreviations.size() == 1 ) {
            EtsEmpAbbreviation etsEmpAbbreviation = etsEmpAbbreviations.get(0);
            if(etsEmpAbbreviation.getAbbreviation() == null && etsEmpAbbreviation.getDefinition() == null) {
                return false;
            }
        }
        return true;
    }
}
