package uk.gov.pmrv.api.migration.notes;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.FileConstants;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.service.FileScanService;
import uk.gov.pmrv.api.files.notes.domain.FileNote;
import uk.gov.pmrv.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.files.FileAttachmentMigrationError;
import uk.gov.pmrv.api.migration.ftp.FtpFileDTOResult;
import uk.gov.pmrv.api.migration.ftp.FtpFileService;
import uk.gov.pmrv.api.migration.ftp.FtpProperties;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * This service can be run once after AccountNoteMigrationService and RequestNoteMigrationService
 * in order to migrate the files for both types of notes
 * */

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class NoteFileMigrationService {
    private final FileNoteRepository fileNoteRepository;
    private final FileScanService fileScanService;
    private final FtpFileService ftpService;
    private final FtpProperties ftpProperties;

    @Transactional
    public Optional<FileAttachmentMigrationError> migrateNoteFile(final String fileUuid) {

        // 1. fetch existing (temp) note file from PMRV DB
        final Optional<FileNote> fileNoteOptional = fileNoteRepository.findByUuid(fileUuid);
        if (fileNoteOptional.isEmpty()) {
            return Optional.of(FileAttachmentMigrationError.builder()
                    .fileAttachmentUuid(fileUuid)
                    .errorReport("Note file not found in the PMRV DB")
                    .build());
        }
        final FileNote fileNote = fileNoteOptional.get();
        if (fileNote.getStatus() != FileStatus.PENDING_MIGRATION) {
            return Optional.of(FileAttachmentMigrationError.builder()
                    .fileAttachmentUuid(fileUuid)
                    .fileName(fileNote.getFileName())
                    .fileContent(fileNote.getFileContent())
                    .errorReport("Note file is not in PENDING_MIGRATION status")
                    .build());
        }

        // 2. Fetch actual file from ETS FTP server
        final String ftpServertDirectory = ftpProperties.getServerNoteFileDirectory();
        final String fileStoredName = new String(fileNote.getFileContent(), StandardCharsets.UTF_8);
        final String filePath = ftpServertDirectory + "/" + fileStoredName;
        final FtpFileDTOResult etsFtpFileDTOResult = ftpService.fetchFile(filePath);
        if (etsFtpFileDTOResult.getErrorReport() != null) {
            return Optional.of(FileAttachmentMigrationError.builder()
                    .fileAttachmentUuid(fileUuid)
                    .fileName(fileNote.getFileName())
                    .fileContent(fileNote.getFileContent())
                    .fileDTO(etsFtpFileDTOResult.getFileDTO())
                    .errorReport(etsFtpFileDTOResult.getErrorReport()).build());
        }

        FileDTO etsFileDTO = etsFtpFileDTOResult.getFileDTO();

        // 3. Validate file
        try {
            validateFileDTO(etsFileDTO);
        } catch (Exception e) {
            return Optional.of(FileAttachmentMigrationError.builder()
                    .fileAttachmentUuid(fileUuid)
                    .fileName(fileNote.getFileName())
                    .fileContent(fileNote.getFileContent())
                    .errorReport(e.getMessage())
                    .build());
        }

        // 4. update note file with actual values fetched from ETS FTP server
        fileNote.setFileContent(etsFileDTO.getFileContent());
        fileNote.setFileSize(etsFileDTO.getFileSize());
        fileNote.setFileType(etsFileDTO.getFileType());
        fileNote.setStatus(FileStatus.SUBMITTED);
        fileNoteRepository.save(fileNote);

        return Optional.empty();
    }

    private void validateFileDTO(final FileDTO etsFileDTO) throws Exception {

        final long fileSize = etsFileDTO.getFileSize();
        if (fileSize <= FileConstants.MIN_FILE_SIZE) {
            throw new Exception(ErrorCode.MIN_FILE_SIZE_ERROR.getMessage());
        }
        if (fileSize >= FileConstants.MAX_FILE_SIZE) {
            throw new Exception(ErrorCode.MAX_FILE_SIZE_ERROR.getMessage());
        }

        try {
            fileScanService.scan(new ByteArrayInputStream(etsFileDTO.getFileContent()));
        } catch (Exception e) {
            throw new Exception(ErrorCode.INFECTED_STREAM.getMessage());
        }
    }
}
