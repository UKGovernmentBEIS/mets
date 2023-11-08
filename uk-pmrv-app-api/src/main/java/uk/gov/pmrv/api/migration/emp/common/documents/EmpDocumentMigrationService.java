package uk.gov.pmrv.api.migration.emp.common.documents;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.ftp.FtpFileDTOResult;
import uk.gov.pmrv.api.migration.ftp.FtpFileService;
import uk.gov.pmrv.api.migration.ftp.FtpProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@Log4j2
@RequiredArgsConstructor
public class EmpDocumentMigrationService {

    private final Validator validator;
    private final EmissionsMonitoringPlanRepository empRepository;
    private final FtpFileService ftpFileService;
    private final FileDocumentService fileDocumentService;
    private final FtpProperties ftpProperties;

    public List<String> migrate(final EtsEmpDocument document, final AtomicInteger failedCounter) {
        final List<String> results = new ArrayList<>();

        final boolean valid = validateDocument(document, failedCounter, results);
        if (!valid) {
            return results;
        }

        try {
            // download document
            final String filePath = ftpProperties.getServerEmpDocumentDirectory() + "/" + document.getEtsFilename();
            final FtpFileDTOResult file = ftpFileService.fetchFile(filePath);

            //create document in db
            fileDocumentService.createFileDocumentWithUuid(
                file.getFileDTO().getFileContent(),
                document.getMigratedFilename(),
                document.getId().toLowerCase()
            );

            // add document id to emp
            empRepository.updateFileDocumentUuidByAccountId(document.getId().toLowerCase(), document.getAccountId());

            results.add("emitterDisplayId: " + document.getAccountId());

        } catch (Exception e) {
            results.add("ERROR: migration of emp document failed for account " + document.getAccountId() +
                " with " + ExceptionUtils.getRootCause(e).getMessage());
            failedCounter.incrementAndGet();
            log.error("migration of emp document failed for account {} with {}",
                document.getAccountId(), ExceptionUtils.getRootCause(e).getMessage());
        }

        return results;
    }

    private boolean validateDocument(final EtsEmpDocument document,
                                     final AtomicInteger failedCounter,
                                     final List<String> results) {
        // validate data
        final Set<ConstraintViolation<EtsEmpDocument>> constraintViolations = validator.validate(document);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v ->
                results.add(
                    "ERROR: " + v.getPropertyPath() + " " + v.getMessage() +
                        " | emitterDisplayId: " + document.getAccountId()

                ));
            failedCounter.incrementAndGet();
            return false;
        }

        // check that emp exists
        final Long accountId = document.getAccountId();
        final Optional<EmissionsMonitoringPlanEntity> empEntity = empRepository.findByAccountId(accountId);
        if (empEntity.isEmpty()) {
            results.add("ERROR: EMP does not exist | emitterDisplayId: " + document.getAccountId());
            failedCounter.incrementAndGet();
            return false;
        }

        return true;
    }
}
