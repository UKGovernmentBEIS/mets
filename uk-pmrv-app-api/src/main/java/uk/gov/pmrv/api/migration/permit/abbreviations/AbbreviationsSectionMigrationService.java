package uk.gov.pmrv.api.migration.permit.abbreviations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class AbbreviationsSectionMigrationService implements PermitSectionMigrationService<Abbreviations> {

    private final JdbcTemplate migrationJdbcTemplate;
    private final MigrationAbbreviationsMapper migrationAbbreviationsMapper;

    private static final String QUERY_BASE  = """
        with XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd),
        allPermits as (
               select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID
                 from tblForm F
                 join tblFormData FD on FD.fldFormID = F.fldFormID
                 join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID
                 join tlkpFormType FT on FTP.fldFormTypeID = FT.fldFormTypeID
                 join tlkpPhase P on FTP.fldPhaseID = P.fldPhaseID
                where FD.fldMinorVersion = 0
                  and P.fldDisplayName = 'Phase 3'
                  and FT.fldName = 'IN_PermitApplication'
        ), mxPVer as (
               select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion'
                 from allPermits
                group by fldFormID
        ), latestPermit as (
               select p.fldEmitterID, FD.*
                 from allPermits p
                 join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion
                 join tblFormData FD on FD.fldFormDataID = p.fldFormDataID
        )
        select e.fldEmitterID as emitterId,
               T.c.query('Ab_abbreviation').value('.', 'NVARCHAR(MAX)') AS abbreviation,
               T.c.query('Ab_definition').value('.', 'NVARCHAR(MAX)') AS definition
          from tblEmitter e
          join tlkpEmitterStatus es on e.fldEmitterStatusID = es.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
          join latestPermit f on e.fldEmitterID = f.fldEmitterID
         outer apply f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ab_abbreviations/row') T(c)
        """;
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, Abbreviations> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setAbbreviations(section));        
    }

    @Override
    public Map<String, Abbreviations> queryEtsSection(List<String> accountIds) {
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);

        Map<String, List<EtsAbbreviation>> etsAbbreviations = executeQuery(query, accountIds);

        return etsAbbreviations.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> toPmrvAbbreviations(entry.getValue())));
    }

    private Map<String, List<EtsAbbreviation>> executeQuery(String query, List<String> accountIds) {
        List<EtsAbbreviation> etsAbbreviations = migrationJdbcTemplate.query(
                query,
                new EtsAbbreviationRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsAbbreviations
            .stream()
            .collect(Collectors.groupingBy(EtsAbbreviation::getEtsAccountId));
    }

    private Abbreviations toPmrvAbbreviations(List<EtsAbbreviation> etsAbbreviations) {
        Abbreviations abbreviations = Abbreviations.builder()
            .exist(false)
            .build();

        if(existAbbreviations(etsAbbreviations)) {
            abbreviations.setExist(true);
            abbreviations.setAbbreviationDefinitions(migrationAbbreviationsMapper.toAbbreviationDefinitions(etsAbbreviations));
        };

        return abbreviations;
    }

    private boolean existAbbreviations(List<EtsAbbreviation> etsAbbreviations) {
        if (etsAbbreviations.isEmpty()) {
            return false;
        }

        if(etsAbbreviations.size() == 1 ) {
            EtsAbbreviation etsAbbreviation = etsAbbreviations.get(0);
            if(etsAbbreviation.getAbbreviation() == null && etsAbbreviation.getDefinition() == null) {
                return false;
            }
        }

        return true;
    }

}
