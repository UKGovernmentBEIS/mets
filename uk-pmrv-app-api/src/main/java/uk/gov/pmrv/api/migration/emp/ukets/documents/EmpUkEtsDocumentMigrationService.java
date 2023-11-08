package uk.gov.pmrv.api.migration.emp.ukets.documents;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
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
@RequiredArgsConstructor
public class EmpUkEtsDocumentMigrationService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final EmpDocumentMigrationService empDocumentMigrationService;

    private static final String QUERY_BASE = "with XMLNAMESPACES(\r\n " +
        " 'urn:www-toplev-com:officeformsofd' AS fd\r\n" +
        "  ),r1 as (\r\n" +
        "   select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,\r\n" +
        "       max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion, fd.fldApprovedPdfAttachmentID\r\n" +
        "   from tblEmitter e\r\n" +
        "   join tblForm F             on f.fldEmitterID = e.fldEmitterID\r\n" +
        "   join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\r\n" +
        "   join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n" +
        "   join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\r\n" +
        "   join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\r\n" +
        "  ), r2 as (\r\n" +
        "   select r1.fldEmitterID, r1.fldEmitterDisplayID, fd.fldMajorVersion, fd.fldApprovedPdfAttachmentID, fd.fldOutputDisplayName,\r\n" +
        "   nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts\r\n" +
        "   from r1\r\n" +
        "   join tblFormData fd on fd.fldFormDataID = r1.fldFormDataID\r\n" +
        "   where r1.fldMajorVersion = r1.maxMajorVersion\r\n" +
        "  )\r\n" +
        "   select r2.fldEmitterID, r2.fldEmitterDisplayID, a.fldAttachmentID, a.fldFileLocation, a.fldFileName, r2.fldOutputDisplayName + ' v' + convert(varchar(10), r2.fldMajorVersion) + '.0.pdf' filename\r\n" +
        "   from tblEmitter e\r\n" +
        "   join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID and ae.fldCommissionListName = 'UK ETS'\r\n" +
        "   join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n" +
        "   join r2 on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'\r\n" +
        "   join tblAttachment a on a.fldAttachmentID = r2.fldApprovedPdfAttachmentID";

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
