package uk.gov.pmrv.api.bulkdownload.accountfileattachment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;

@ExtendWith(MockitoExtension.class)
class AlrBulkDownloadGenerateFileServiceTest {

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @InjectMocks
    private AlrBulkDownloadGenerateFileService alrBulkDownloadGenerateFileService;

    @Test
    void prepareZipFileWithStructure_createsCorrectZipEntries() throws Exception {
        AccountFileAttachmentDTO reportDto = mock(AccountFileAttachmentDTO.class);
        when(reportDto.getWorkflowSubtype()).thenReturn(AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT);
        when(reportDto.getFileUuid()).thenReturn("UUID_REPORT");

        AccountFileAttachmentDTO vosDto = mock(AccountFileAttachmentDTO.class);
        when(vosDto.getWorkflowSubtype()).thenReturn(AccountFileAttachmentWorkflowSubType.ALR_VOS);
        when(vosDto.getFileUuid()).thenReturn("UUID_VOS");
        when(vosDto.getOriginatedRequestId()).thenReturn("ALR00002");

        when(accountFileAttachmentService.getFilesByWorkflowAndPeriodAndCompetentAuthority(
            AccountFileAttachmentWorkflow.ALR,
            "2024",
            CompetentAuthorityEnum.ENGLAND
        )).thenReturn(List.of(reportDto, vosDto));

        FileDTO reportFile = new FileDTO();
        reportFile.setFileName("report.pdf");
        reportFile.setFileContent("REPORT".getBytes());

        FileDTO vosFile = new FileDTO();
        vosFile.setFileName("vos.pdf");
        vosFile.setFileContent("VOS".getBytes());

        when(fileAttachmentService.getFileDTO("UUID_REPORT")).thenReturn(reportFile);
        when(fileAttachmentService.getFileDTO("UUID_VOS")).thenReturn(vosFile);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        alrBulkDownloadGenerateFileService.generateFile(
            zipOut,
            "2024",
            CompetentAuthorityEnum.ENGLAND
        );

        zipOut.close();

        // ---- verify ZIP content ----
        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
        List<String> entryNames = new ArrayList<>();
        ZipEntry entry;

        while ((entry = zipIn.getNextEntry()) != null) {
            entryNames.add(entry.getName());
        }

        assertThat(entryNames).containsExactlyInAnyOrder(
            "2024_ALR_EA/",
            "2024_ALR_EA/ALR reports/",
            "2024_ALR_EA/ALR VOS/",
            "2024_ALR_EA/ALR reports/report.pdf",
            "2024_ALR_EA/ALR VOS/ALR00002-VOS-vos.pdf"
        );
    }

}