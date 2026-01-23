package uk.gov.pmrv.api.bulkdownload.accountfileattachment.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;

@Slf4j
@RequiredArgsConstructor
@Service
class AlrBulkDownloadGenerateFileService {

    private final FileAttachmentService fileAttachmentService;
    private final AccountFileAttachmentService accountFileAttachmentService;

    void generateFile(ZipOutputStream zipOut, String period, CompetentAuthorityEnum competentAuthority) throws
        IOException {

        List<AccountFileAttachmentDTO> fileDTOs = accountFileAttachmentService
            .getFilesByWorkflowAndPeriodAndCompetentAuthority(AccountFileAttachmentWorkflow.ALR, period, competentAuthority);

        String root = period + "_ALR_" + competentAuthority.getCode() + "/";

        zipOut.putNextEntry(new ZipEntry(root));
        zipOut.closeEntry();

        zipOut.putNextEntry(new ZipEntry(root + "ALR reports/"));
        zipOut.closeEntry();

        zipOut.putNextEntry(new ZipEntry(root + "ALR VOS/"));
        zipOut.closeEntry();

        for (AccountFileAttachmentDTO fileDTO : fileDTOs) {

            FileDTO file = fileAttachmentService.getFileDTO(fileDTO.getFileUuid());
            byte[] fileContent = file.getFileContent();

            String subfolder = switch (fileDTO.getWorkflowSubtype()) {
                case ALR_ATTACHMENT -> "ALR reports/";
                case ALR_VOS -> "ALR VOS/";
                default -> throw new IllegalArgumentException("Unknown ALR workflow subtype: " + fileDTO.getWorkflowSubtype());
            };

            String finalFilename = buildFinalFilename(fileDTO, file);

            zipOut.putNextEntry(new ZipEntry(root + subfolder + finalFilename));

            try (ByteArrayInputStream bais = new ByteArrayInputStream(fileContent)) {
                byte[] buffer = new byte[8192];
                int length;

                while ((length = bais.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }

            } catch (IOException ex) {
                log.error("Error writing {} to ZIP", file.getFileName(), ex);
                throw ex;
            }

            zipOut.closeEntry();
        }
    }

    private String buildFinalFilename(AccountFileAttachmentDTO fileDTO, FileDTO file) {

        // Default: use original filename
        String originalName = file.getFileName();

        // Only rename for VOS subtype
        if (fileDTO.getWorkflowSubtype() == AccountFileAttachmentWorkflowSubType.ALR_VOS) {

            String workflowId = fileDTO.getOriginatedRequestId();   // e.g., ALR00002

            return workflowId + "-" + "VOS-" + originalName;
        }

        return originalName;
    }

}
