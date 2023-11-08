package uk.gov.pmrv.api.migration.permit.document;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.ftp.FtpFileDTOResult;
import uk.gov.pmrv.api.migration.ftp.FtpFileService;
import uk.gov.pmrv.api.migration.ftp.FtpProperties;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.repository.PermitRepository;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@Log4j2
@RequiredArgsConstructor
public class MigrationPermitDocumentService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;
    private final PermitRepository permitRepository;
    private final FtpFileService ftpFileService;
    private final FileDocumentService fileDocumentService;
    private final FtpProperties ftpProperties;
    

    private static final String QUERY_BASE = """
        with XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd),
        allPermits as (
            select f.fldEmitterID, f.fldFormID,
                   fd.fldMajorVersion, max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion,
                   fd.fldDateUpdated, fd.fldFormDataID
              from tblForm f
              join tblFormData fd on fd.fldFormID = f.fldFormID            and fd.fldMinorVersion = 0
              join tlnkFormTypePhase ftp on ftp.fldFormTypePhaseID = f.fldFormTypePhaseID
              join tlkpFormType ft on ft.fldFormTypeID = ftp.fldFormTypeID and ft.fldName = 'IN_PermitApplication'
              join tlkpPhase p on p.fldPhaseID = ftp.fldPhaseID            and p.fldDisplayName = 'Phase 3'
        ), latestPermit as (
            select p.fldEmitterID, e.fldEmitterDisplayID, fd.*
              from allPermits p
              join tblFormData fd on fd.fldFormDataID = p.fldFormDataID
              join tblEmitter e on e.fldEmitterID = p.fldEmitterID
              join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID and (es.fldDisplayName = 'Live' or p.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
              join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID and et.fldName = 'Installation'
             where p.fldMajorVersion = p.maxMajorVersion
        )
        select p.fldEmitterID, p.fldEmitterDisplayID, a.fldAttachmentID, a.fldFileLocation, a.fldFileName, p.fldOutputDisplayName + ' v' + convert(varchar(10), p.fldMajorVersion) + '.0.pdf' filename
          from latestPermit p
          join tblAttachment a on a.fldAttachmentID = p.fldApprovedPdfAttachmentID
    """;


    @Override
    public String getResource() {
        return "permit-documents";
    }

    @Override
    public List<String> migrate(String ids) {

        final String query = this.constructQuery(QUERY_BASE, ids);
        final List<String> results = new ArrayList<>();
        final List<PermitDocument> documents = migrationJdbcTemplate.query(query, new PermitDocumentMapper());

        final AtomicInteger failedCounter = new AtomicInteger(0);
        for (final PermitDocument document : documents) {
            List<String> migrationResults = this.doMigrate(document, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of permit documents: " + failedCounter + "/" + documents.size() + " failed");
        return results;
    }

    private String constructQuery(String query, String ids) {

        final StringBuilder queryBuilder = new StringBuilder(query);

        final List<String> idList = !StringUtils.isBlank(ids) ?
            new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();

        if (!idList.isEmpty()) {
            queryBuilder.append(" where fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        return queryBuilder.toString();
    }

    private List<String> doMigrate(final PermitDocument document, final AtomicInteger failedCounter) {

        final List<String> results = new ArrayList<>();

        final boolean valid = this.validateDocument(document, failedCounter, results);
        if (!valid) {
            return results;
        }

        try {
            // download document
            final String filePath = ftpProperties.getServerPermitDocumentDirectory() + "/" + document.getEtsFilename();
            final FtpFileDTOResult file = ftpFileService.fetchFile(filePath);
            fileDocumentService.createFileDocumentWithUuid(
                file.getFileDTO().getFileContent(),
                document.getFilename(),
                document.getId().toLowerCase()
            );

            // add document id to permit
            permitRepository.updateFileDocumentUuidByAccountId(document.getAccountId(), document.getId().toLowerCase());
            
            results.add("emitterDisplayId: " + document.getAccountId());

        } catch (Exception e) {
            results.add("ERROR: migration of permit document failed for account " + document.getAccountId() +
                " with " + ExceptionUtils.getRootCause(e).getMessage());
            failedCounter.incrementAndGet();
            log.error("migration of permit document failed for account {} with {}",
                document.getAccountId(), ExceptionUtils.getRootCause(e).getMessage());
        }

        return results;
    }

    private boolean validateDocument(final PermitDocument document, 
                                     final AtomicInteger failedCounter,
                                     final List<String> results) {
        // validate data
        final Set<ConstraintViolation<PermitDocument>> constraintViolations = validator.validate(document);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v ->
                results.add(
                    "ERROR: " + v.getPropertyPath() + " " + v.getMessage() +
                        " | emitterDisplayId: " + document.getAccountId()

                ));
            failedCounter.incrementAndGet();
            return false;
        }

        // check that permit exists
        final Long accountId = document.getAccountId();
        final Optional<PermitEntity> permit = permitRepository.findByAccountId(accountId);
        if (permit.isEmpty()) {
            results.add("ERROR: Permit does not exist | emitterDisplayId: " + document.getAccountId());
            failedCounter.incrementAndGet();
            return false;
        }
        
        return true;
    }
}