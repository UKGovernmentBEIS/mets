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
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;

@Slf4j
@RequiredArgsConstructor
@Service
class Bdrs2BulkDownloadGenerateFileService {

    private static final String BDRS2_FILES_FOLDER = "Stage 2 BDR files/";
    private static final String VOS_FOLDER = "Stage 2 BDR VOS/";
    private static final String MMP_FOLDER = "Stage 2 BDR MMP files/";

    private final FileAttachmentService fileAttachmentService;
    private final AccountFileAttachmentService accountFileAttachmentService;

    void generateFile(ZipOutputStream zipOut, String period, CompetentAuthorityEnum competentAuthority) throws
        IOException {

        List<AccountFileAttachmentDTO> fileDTOs = accountFileAttachmentService
            .getFilesByWorkflowAndPeriodAndCompetentAuthority(AccountFileAttachmentWorkflow.BDRS2, period, competentAuthority);

        String root = period + " Stage 2 BDR " + competentAuthority.getCode() + "/";

        zipOut.putNextEntry(new ZipEntry(root));
        zipOut.closeEntry();

        zipOut.putNextEntry(new ZipEntry(root + BDRS2_FILES_FOLDER));
        zipOut.closeEntry();

        zipOut.putNextEntry(new ZipEntry(root + VOS_FOLDER));
        zipOut.closeEntry();

        zipOut.putNextEntry(new ZipEntry(root + MMP_FOLDER));
        zipOut.closeEntry();

        for (AccountFileAttachmentDTO fileDTO : fileDTOs) {

            FileDTO file = fileAttachmentService.getFileDTO(fileDTO.getFileUuid());
            byte[] fileContent = file.getFileContent();

            String subfolder = switch (fileDTO.getWorkflowSubtype()) {
                case BDR_ATTACHMENT -> BDRS2_FILES_FOLDER;
                case BDRS2_VOS -> VOS_FOLDER;
                case BDRS2_MMP -> MMP_FOLDER;
                default -> throw new IllegalArgumentException("Unknown BDRS2 workflow subtype: " + fileDTO.getWorkflowSubtype());
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
        String originalName = file.getFileName();
        String workflowId = fileDTO.getOriginatedRequestId();

        return switch (fileDTO.getWorkflowSubtype()) {
            case BDR_ATTACHMENT -> originalName;
            case BDRS2_VOS -> workflowId + "-VOS-" + originalName;
            case BDRS2_MMP -> workflowId + "-MMP-" + originalName;
            default -> throw new IllegalArgumentException("Unknown BDRS2 workflow subtype: " + fileDTO.getWorkflowSubtype());
        };
    }
}
