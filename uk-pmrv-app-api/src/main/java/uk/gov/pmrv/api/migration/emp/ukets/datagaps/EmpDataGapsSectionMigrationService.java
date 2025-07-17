package uk.gov.pmrv.api.migration.emp.ukets.datagaps;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpDataGapsSectionMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpDataGaps> {

    private final JdbcTemplate migrationJdbcTemplate;

    public EmpDataGapsSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
    }

    private static final String QUERY_BASE = "with XMLNAMESPACES (\r\n" +
        " 'urn:www-toplev-com:officeformsofd' AS fd\r\n" +
        "), r1 as (\r\n" +
        "    select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,\r\n" +
        "           max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion\r\n" +
        "      from tblEmitter e\r\n" +
        "      join tblForm F             on f.fldEmitterID = e.fldEmitterID\r\n" +
        "      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\r\n" +
        "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n" +
        "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\r\n" +
        "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\r\n" +
        "), r2 as (\r\n" +
        "    select r1.*,\r\n" +
        "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts\r\n" +
        "      from r1\r\n" +
        "     where fldMajorVersion = maxMajorVersion \r\n" +
        ")\r\n" +
        "select e.fldEmitterID, e.fldEmitterDisplayID,\n\n" +
        "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Threshold_data_gap      )[1]', 'nvarchar(max)')), '') Threshold_data_gap     ,\r\n" +
        "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Secondary_data_gap      )[1]', 'nvarchar(max)')), '') Secondary_data_gap     ,\r\n" +
        "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Alternative_method_used )[1]', 'nvarchar(max)')), '') Alternative_method_used,\r\n" +
        "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_gap_methodology    )[1]', 'nvarchar(max)')), '') Data_gap_methodology\r\n" +
        "  from tblEmitter e\r\n" +
        "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n" +
        "                            and ae.fldCommissionListName = 'UK ETS'\r\n" +
        "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n" +
        "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n" +
        "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {
        Map<String, EmpDataGaps> dataGapsSections = queryEtsSection(new ArrayList<>(accountsToMigrate.keySet()));

        dataGapsSections.forEach((etsAccId, section) ->
                emps.get(accountsToMigrate.get(etsAccId).getId()).getEmpContainer().getEmissionsMonitoringPlan().setDataGaps(section));
    }

    @Override
    public Map<String, EmpDataGaps> queryEtsSection(List<String> accountIds) {
        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpDataGaps> etsDataGapsSections = executeQuery(query, accountIds);

        return etsDataGapsSections.stream()
            .filter(data -> ObjectUtils.isNotEmpty(data.getThresholdDataGap()) ||
                ObjectUtils.isNotEmpty(data.getSecondaryDataGap()) ||
                ObjectUtils.isNotEmpty(data.getAlternativeMethodUsed()) ||
                ObjectUtils.isNotEmpty(data.getDataGapMethodology())
            )
            .collect(Collectors.toMap(EtsEmpDataGaps::getFldEmitterId, EmpDataGapsMigrationMapper::toEmpDataGaps));
    }

    private List<EtsEmpDataGaps> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query, new EtsEmpDataGapsRowMapper(), accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
