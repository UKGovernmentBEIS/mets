package uk.gov.pmrv.api.migration.emp.corsia.datagaps;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.datagaps.EmpDataGapsCorsia;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpDataGapsCorsiaSectionMigrationService implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpDataGapsCorsia> {

	private final JdbcTemplate migrationJdbcTemplate;

	public EmpDataGapsCorsiaSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
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
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_data_gap_secondary_source )[1]', 'nvarchar(max)')), '') Corsia_data_gap_secondary_source,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_data_gap_handling         )[1]', 'nvarchar(max)')), '') Corsia_data_gap_handling,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_data_gap_procedure        )[1]', 'nvarchar(max)')), '') Corsia_data_gap_procedure,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_data_gap_secondary_source )[1]', 'nvarchar(max)')), '') Corsia_data_gap_secondary_source,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_data_gap_option           )[1]', 'nvarchar(max)')), '') Corsia_data_gap_option,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_data_gap_explanation      )[1]', 'nvarchar(max)')), '') Corsia_data_gap_explanation\r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeCorsia = 'true'\r\n"
    		+ "  left join mig_aviation_emitters m on m.fldEmitterID = e.fldEmitterID and m.scope = 'CORSIA'\r\n";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {	
    	List<String> accountIds = new ArrayList<>(accountsToMigrate.keySet());
    	String query = constructSectionQuery(QUERY_BASE, accountIds);
    	
    	List<EtsEmpDataGapsCorsia> dataGapsSections = executeQuery(query, accountIds);

        dataGapsSections.forEach((section) -> {
        	EmissionsMonitoringPlanCorsia emp = 
        			emps.get(accountsToMigrate.get(section.getFldEmitterID()).getId()).getEmpContainer().getEmissionsMonitoringPlan();
        	emp.setDataGaps(EmpDataGapsCorsiaMigrationMapper.toEmpDataGaps(section, emp.getEmissionsMonitoringApproach().getMonitoringApproachType()));
        });
    }
    
    @Override
    public Map<String, EmpDataGapsCorsia> querySection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }

    private List<EtsEmpDataGapsCorsia> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query, new EtsEmpDataGapsCorsiaRowMapper(), 
        		accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
