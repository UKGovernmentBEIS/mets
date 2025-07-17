package uk.gov.pmrv.api.migration.ftp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FtpFileService {
    
    private final FtpClient ftpClient;
    
    public void healthCheck() {
        ftpClient.healthCheck();
    }
    
    public FtpFileDTOResult fetchFile(String file) {
        String fileName = null;
        try {
            fileName = file.substring(file.lastIndexOf("/") + 1);
            byte[] fileContent = ftpClient.fetchFile(file);
            return FtpFileDTOResult.builder()
                    .fileDTO(buildFileDTO(fileName, fileContent))
                    .build();
        } catch (FtpException e) {
            return FtpFileDTOResult.builder()
                    .fileDTO(FileDTO.builder().fileName(fileName).build())
                    .errorReport(e.getMessage())
                    .build();
        } catch (Exception e) {
            return FtpFileDTOResult.builder()
                    .fileDTO(FileDTO.builder().fileName(fileName).build())
                    .errorReport("Error occurred when trying to fetch file from FTP server. Reason: " + e.getMessage())
                    .build();
        } 
        
    }

    public List<FtpFileDTOResult> fetchFiles(List<String> files) {
        List<FtpFileDTOResult> ftpFileDTOResult = new ArrayList<>();
        String fileName = null;
        try(FtpClient ftpClient = this.ftpClient){
            for(String file : files) {
                try {
                    fileName = file.substring(file.lastIndexOf("/") + 1);
                    byte[] fileContent = ftpClient.fetchFileBatch(file);
                    ftpFileDTOResult.add(
                            FtpFileDTOResult.builder()
                                .fileDTO(buildFileDTO(fileName, fileContent))
                                .build());
                } catch (FtpException e) {
                    ftpFileDTOResult.add(FtpFileDTOResult.builder()
                            .fileDTO(FileDTO.builder().fileName(fileName).build())
                            .errorReport(e.getMessage())
                            .build());
                } catch (Exception e) {
                    ftpFileDTOResult.add(FtpFileDTOResult.builder()
                            .fileDTO(FileDTO.builder().fileName(fileName).build())
                            .errorReport("Error occurred when trying to fetch file from FTP server. Reason: " + e.getMessage())
                            .build());
                } 
            }
        } catch (Exception e) {
            log.error("Error occurred when trying to fetch files from FTP server", e);
        } 
        return ftpFileDTOResult;
    }

    public List<String> fetchFilePaths(String directory) {
        try {
            return ftpClient.getFilePaths(directory);
        } catch (FtpException e) {
            log.error("Error occurred when trying to fetch file paths from FTP server:", e);
        }
        return new ArrayList<>();
    }
    
    private FileDTO buildFileDTO(String fileName, byte[] fileContent) {
        return FileDTO.builder()
                        .fileName(fileName)
                        .fileType(MimeTypeUtils.detect(fileContent, fileName))
                        .fileContent(fileContent)
                        .fileSize(fileContent.length)
                        .build();
    }
    
}
