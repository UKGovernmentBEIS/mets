package uk.gov.pmrv.api.migration.permit.consolidationnumber;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class PermitConsolidationNumberMigrationService {

    private final JdbcTemplate migrationJdbcTemplate;

    public PermitConsolidationNumberMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
    }

    private static final String QUERY_BASE = "with XMLNAMESPACES ("
        + "'urn:www-toplev-com:officeformsofd' AS fd"
        + "), allPermits as ("
        + "select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID "
        + "from tblForm F "
        + "join tblFormData FD on FD.fldFormID = F.fldFormID "
        + "join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID "
        + "join tlkpFormType FT on FTP.fldFormTypeID = FT.fldFormTypeID "
        + "join tlkpPhase P on FTP.fldPhaseID = P.fldPhaseID "
        + "where FD.fldMinorVersion = 0 "
        + " and P.fldDisplayName = 'Phase 3' "
        + " and FT.fldName = 'IN_PermitApplication' "
        + "), mxPVer as ("
        + "    select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion' "
        + "        from allPermits "
        + "    group by fldFormID "
        + "), latestPermit as ("
        + "    select p.fldEmitterID, MaxMajorVersion PermitVersion "
        + "        from allPermits p "
        + "        join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion "
        + ") "
        + "select E.fldEmitterID as emitterId, PermitVersion "
        + "  from tblEmitter E "
        + "  join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live)) "
        + "  join latestPermit F on E.fldEmitterID = F.fldEmitterID ";

    public void populateConsolidationNumber(Map<String, Account> accountsToMigratePermit,
        Map<Long, PermitMigrationContainer> permits) {
        Map<String, Integer> permitsWithConsolidationNumbers =
            queryEts(new ArrayList<>(accountsToMigratePermit.keySet()));

        permitsWithConsolidationNumbers
            .forEach((etsAccId, version) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId()).setConsolidationNumber(version));
    }

    public Map<String, Integer> queryEts(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where E.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();
        List<EtsPermitConsolidationNumberType> etsPermitTypes = executeQuery(query, accountIds);

        return etsPermitTypes.stream()
            .collect(Collectors.toMap(EtsPermitConsolidationNumberType::getEtsAccountId,
                EtsPermitConsolidationNumberType::getConsolidationNumber));
    }

    private List<EtsPermitConsolidationNumberType> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
            new EtsPermitConsolidationNumberRowMapper(),
            accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }

}
