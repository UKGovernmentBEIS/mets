package uk.gov.pmrv.api.migration.notes;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.notes.domain.FileNote;
import uk.gov.pmrv.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.files.FileAttachmentMigrationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This service can be run once after AccountNoteMigrationService and RequestNoteMigrationService
 * in order to migrate the files for both types of notes
 * */

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class NoteFilesMigrationService extends MigrationBaseService {

    private final FileNoteRepository fileNoteRepository;
    private final NoteFileMigrationService noteFileMigrationService;

    @Override
    public String getResource() {
        return "note-files";
    }

    @Override
    public List<String> migrate(final String fileUuids) {

        final List<String> failedEntries = new ArrayList<>();

        final List<String> fileUuidList = this.collectFileUuidToMigrate(fileUuids);

        for (String fileUuid : fileUuidList) {
            noteFileMigrationService.migrateNoteFile(fileUuid)
                .ifPresent(migrationError -> failedEntries.add(this.buildErrorMessage(migrationError)));
        }

        failedEntries.add("Statistics: Total: " + fileUuidList.size() + ". Failed: " + failedEntries.size());
        return failedEntries;
    }

    private String buildErrorMessage(final FileAttachmentMigrationError migrationError) {
        return String.format("File with uuid %s failed with file report %s",
            migrationError.getFileAttachmentUuid(),
            migrationError.getErrorReport()
        );
    }

    private List<String> collectFileUuidToMigrate(final String fileUuids) {

        List<String> fileUuidList = !StringUtils.isBlank(fileUuids)
            ? new ArrayList<>(Arrays.asList(fileUuids.split("\\s*,\\s*")))
            : new ArrayList<>();

        if (fileUuidList.isEmpty()) {
            fileUuidList = fileNoteRepository.findByStatus(FileStatus.PENDING_MIGRATION)
                .stream().map(FileNote::getUuid).toList();
        }
        return fileUuidList;
    }


}
