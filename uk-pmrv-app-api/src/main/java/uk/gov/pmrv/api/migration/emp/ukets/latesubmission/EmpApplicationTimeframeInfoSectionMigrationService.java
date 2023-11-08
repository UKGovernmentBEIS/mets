package uk.gov.pmrv.api.migration.emp.ukets.latesubmission;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpApplicationTimeframeInfoSectionMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpApplicationTimeframeInfo> {

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
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts\r\n"
    		+ "      from r1\r\n"
    		+ "     where fldMajorVersion = maxMajorVersion \r\n"
    		+ ")\r\n"
    		+ "select e.fldEmitterID, e.fldEmitterDisplayID, ae.fldFirstFlyDate,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Date_aviation_activity_captured)[1]', 'nvarchar(max)')), '') Date_aviation_activity_captured,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Submission_late_yes            )[1]', 'nvarchar(max)')), '') Submission_late_yes,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Submission_late_justifiy       )[1]', 'nvarchar(max)')), '') Submission_late_justify\r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {

        Map<String, EmpApplicationTimeframeInfo> sections =
                queryEtsSection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setApplicationTimeframeInfo(section);
                });
    }


    @Override
    public Map<String, EmpApplicationTimeframeInfo> queryEtsSection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpApplicationTimeframeInfo> etsEmpApplicationTimeframeInfos = executeQuery(query, accountIds);

        return etsEmpApplicationTimeframeInfos.stream()
                .collect(Collectors.toMap(EtsEmpApplicationTimeframeInfo::getFldEmitterId,
                		EmpApplicationTimeframeInfoMigrationMapper::toEmpApplicationTimeframeInfo));

    }

    private List<EtsEmpApplicationTimeframeInfo> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmpApplicationTimeframeInfoRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
