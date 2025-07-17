package uk.gov.pmrv.api.migration.permit.digitizedMmp.documents;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.ftp.FtpFileDTOResult;
import uk.gov.pmrv.api.migration.ftp.FtpFileService;
import uk.gov.pmrv.api.migration.ftp.FtpProperties;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationError;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.MmpFileType;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@WebEndpoint(id = "mmp-migration-docs-upload")
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MmpDocumentMigrationService {
    private final FtpFileService ftpFileService;
    private final FtpProperties ftpProperties;
    private final MmpFilesMigrationRepository mmpFilesMigrationRepository;

    @WriteOperation
    public List<String> migrate(String mode,
                                 @Nullable String directory,
                                 @Nullable String id,
                                 @Nullable String fileName,
                                 @Nullable MmpFileType fileType) {
        List<String> results;
         switch (mode) {
            case "all" -> results = migrate_all(directory);
            case "file" -> results = migrate_file(id, fileName, fileType);
            default -> throw new IllegalArgumentException("Unsupported mode");
        }
         return results;

    }

    private List<String> migrate_all(String directory){
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        final List<String> results = new ArrayList<>();
        String full_directory = ftpProperties.getServerMmpDocumentDirectory() + "/" + directory + "/";
        List<String> filePaths = ftpFileService.fetchFilePaths(full_directory);
        if (filePaths.isEmpty()) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder()
                    .errorReport("The list of file paths returned from the sftp server is empty").build()));
            return results;
        }
        for (String filePath : filePaths) {
            results.add(migrateFile(Path.of(full_directory+filePath)));
        }
        return results;
    }

    public List<String> migrate_file(String id,String fileName, MmpFileType mmpFileType) {
        if (id == null || fileName == null || mmpFileType==null) {
            throw new IllegalArgumentException("id,filename,fileType cannot be null");
        }
        final List<String> results = new ArrayList<>();
        Long accountId;
        try {
            accountId = Long.valueOf(id);
        } catch (NumberFormatException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(id)
                    .errorReport("wrong accountId format!").build()));
            return results;
        }

        FtpFileDTOResult ftpFileDTOResult =
                ftpFileService.fetchFile(ftpProperties.getServerMmpDocumentDirectory() + "/" + fileName);

        if (ftpFileDTOResult == null || ftpFileDTOResult.getErrorReport() != null) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(id)
                    .errorReport("error getting file from FTP: " + ftpFileDTOResult.getErrorReport()).build()));
            return results;
        }

        MmpFilesMigrationEntity mmpFilesMigrationEntity = MmpFilesMigrationEntity.builder()
                .accountId(accountId).fileName(fileName).mmpFileType(mmpFileType).fileContent(ftpFileDTOResult.getFileDTO()
                        .getFileContent())
                .build();

        try {
            mmpFilesMigrationRepository.save(mmpFilesMigrationEntity);
        } catch (Exception e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(id)
                    .errorReport("error saving file to database: " + e.getMessage()).build()));
            return results;
        }

        results.add("OK");
        return results;
    }

    private String migrateFile(Path filePath) {

        FtpFileDTOResult ftpFileDTOResult =
                ftpFileService.fetchFile(String.valueOf(filePath));

        if (ftpFileDTOResult == null || ftpFileDTOResult.getErrorReport() != null) {
            return DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder()
                    .errorReport("error getting file from FTP").build());
        }

        String account_id = filePath.getParent().getParent().getFileName().toString();
        String fileType = filePath.getParent().getFileName().toString();
        String fileName = filePath.getFileName().toString();
        MmpFilesMigrationEntity mmpFilesMigrationEntity = MmpFilesMigrationEntity.builder()
                .accountId(Long.valueOf(account_id)).fileName(fileName).mmpFileType(MmpFileType.valueOf(fileType)).fileContent(ftpFileDTOResult.getFileDTO()
                        .getFileContent())
                .build();

        try {
            mmpFilesMigrationRepository.save(mmpFilesMigrationEntity);
        } catch (Exception e) {
           return DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(account_id)
                    .errorReport("error saving file to database: " + e.getMessage()).build());
        }
        return fileName+" - OK";
    }

}
