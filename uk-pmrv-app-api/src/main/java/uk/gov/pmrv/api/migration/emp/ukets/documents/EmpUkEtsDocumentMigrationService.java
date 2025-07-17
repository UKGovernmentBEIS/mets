package uk.gov.pmrv.api.migration.emp.ukets.documents;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.documents.EmpDocumentMigrationService;
import uk.gov.pmrv.api.migration.emp.common.documents.EtsEmpDocument;
import uk.gov.pmrv.api.migration.emp.common.documents.EtsEmpDocumentRowMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
public class EmpUkEtsDocumentMigrationService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final EmpDocumentMigrationService empDocumentMigrationService;

    public EmpUkEtsDocumentMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                            EmpDocumentMigrationService empDocumentMigrationService) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.empDocumentMigrationService = empDocumentMigrationService;
    }

    private static final String QUERY_BASE = """
            with XMLNAMESPACES(
         'urn:www-toplev-com:officeformsofd' AS fd
          ),r1 as (
           select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,
               max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion, fd.fldApprovedPdfAttachmentID
           from tblEmitter e
          join tblForm F             on f.fldEmitterID = e.fldEmitterID
           join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0
           join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID
           join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'
           join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'
         ), r2 as (
           select r1.fldEmitterID, r1.fldEmitterDisplayID, fd.fldMajorVersion, fd.fldApprovedPdfAttachmentID, fd.fldOutputDisplayName,
           nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts
           from r1
           join tblFormData fd on fd.fldFormDataID = r1.fldFormDataID
           where r1.fldMajorVersion = r1.maxMajorVersion
          )
           select r2.fldEmitterID, r2.fldEmitterDisplayID, a.fldAttachmentID, a.fldFileLocation, a.fldFileName, r2.fldOutputDisplayName + ' v' + convert(varchar(10), r2.fldMajorVersion) + '.0.pdf' filename
          from tblEmitter e
           join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID and ae.fldCommissionListName = 'UK ETS'
           join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')
          join r2 on r2.fldEmitterID = e.fldEmitterID
          join tblAttachment a on a.fldAttachmentID = r2.fldApprovedPdfAttachmentID
          """;

    @Override
    public String getResource() {
        return "emp-ukets-documents";
    }

    @Override
    public List<String> migrate(String ids) {
        final String query = constructQuery(QUERY_BASE, ids);
        final List<String> results = new ArrayList<>();
        final List<EtsEmpDocument> documents = migrationJdbcTemplate.query(query, new EtsEmpDocumentRowMapper());

        final AtomicInteger failedCounter = new AtomicInteger(0);
        for (final EtsEmpDocument document : documents) {
            List<String> migrationResults = empDocumentMigrationService.migrate(document, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of emp ukets documents: " + failedCounter + "/" + documents.size() + " failed");
        return results;
    }

    private static String constructQuery(String query, String ids) {
        final StringBuilder queryBuilder = new StringBuilder(query);

        final List<String> idList = !StringUtils.isBlank(ids) ?
                new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();

        if (!idList.isEmpty()) {
            queryBuilder.append(" where r2.fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        return queryBuilder.toString();
    }
}
