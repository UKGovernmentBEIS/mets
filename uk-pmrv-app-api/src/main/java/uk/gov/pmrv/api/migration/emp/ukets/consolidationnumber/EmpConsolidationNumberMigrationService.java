package uk.gov.pmrv.api.migration.emp.ukets.consolidationnumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
@Log4j2
public class EmpConsolidationNumberMigrationService extends MigrationBaseService {

    private static final String EMP_CONSOLIDATION_NUMBER_QUERY_BASE =
        """
            with XMLNAMESPACES (
                'urn:www-toplev-com:officeformsofd' AS fd
            ), r1 as (
                select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,
                       max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion
                  from tblEmitter e
                  join tblForm F             on f.fldEmitterID = e.fldEmitterID
                  join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0
                  join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID
                  join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'
                  join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'
            ), r2 as (
                select r1.*,
                       nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts
                  from r1
                 where fldMajorVersion = maxMajorVersion
            )
            select e.fldEmitterID, e.fldEmitterDisplayID, fldMajorVersion empVersion
              from tblEmitter e
              join tblAviationEmitter ae on ae.fldEmitterID  = e.fldEmitterID
                                        and ae.fldCommissionListName = 'UK ETS'
              join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID
                                        and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')
              join      r2               on r2.fldEmitterID  = e.fldEmitterID and scopeUkEts = 'true'
              """;

    private final JdbcTemplate migrationJdbcTemplate;
    private final AccountRepository accountRepository;
    private final EmissionsMonitoringPlanRepository empRepository;

    @Override
    public String getResource() {
        return "emps-ukets-version";
    }

    @Override
    public List<String> migrate(String ids) {

        final String query = this.constructQuery(EMP_CONSOLIDATION_NUMBER_QUERY_BASE, ids);
        final List<String> results = new ArrayList<>();
        final List<EtsEmpVersionRow> consolidationNumberRows = migrationJdbcTemplate.query(query, new EtsEmpVersionMapper());

        final AtomicInteger failedCounter = new AtomicInteger(0);
        for (final EtsEmpVersionRow consolidationNumber : consolidationNumberRows) {
            List<String> migrationResults = this.doMigrate(consolidationNumber, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of emp ukets consolidation numbers requests: " + failedCounter + "/" + consolidationNumberRows.size() + " failed");
        return results;
    }

    private String constructQuery(String query, String ids) {

        final StringBuilder queryBuilder = new StringBuilder(query);

        final List<String> idList = !StringUtils.isBlank(ids) ?
            new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();

        if (!idList.isEmpty()) {
            queryBuilder.append(" and e.fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        return queryBuilder.toString();
    }

    private boolean validateEmp(final EtsEmpVersionRow empVersionRow, final AtomicInteger failedCounter,
                                   final List<String> results) {

        // check that account id exists
        final Long accountId = empVersionRow.getAccountId();
        if (!accountRepository.existsById(accountId)) {
            results.add("ERROR: Account id does not exist | emitterDisplayId: " + accountId);
            failedCounter.incrementAndGet();
            return false;
        }

        // check that emp exists
        empRepository.findEmpIdByAccountId(accountId)
            .map(empId -> true)
            .orElseGet(() -> {
                results.add("ERROR: Emp id does not exist | emitterDisplayId: " + accountId);
                failedCounter.incrementAndGet();
                return false;
            });

        return true;
    }

    private List<String> doMigrate(final EtsEmpVersionRow empVersionRow, final AtomicInteger failedCounter) {

        final List<String> results = new ArrayList<>();

        final boolean valid = this.validateEmp(empVersionRow, failedCounter, results);
        if (!valid) {
            return results;
        }

        try {
            empRepository.updateConsolidationNumberByAccountId(empVersionRow.getConsolidationNumber(), empVersionRow.getAccountId());
        } catch (Exception e) {
            // this should not be reached, as the data has already been validated
            results.add("ERROR: migration of emp ukets consolidation number failed for account " + empVersionRow.getAccountId() +
                ExceptionUtils.getRootCause(e).getMessage());
            failedCounter.incrementAndGet();
            log.error("migration of emp ukets consolidation number failed for account {} with {}",
                empVersionRow.getAccountId(), ExceptionUtils.getRootCause(e).getMessage());
        }
        return results;
    }
}