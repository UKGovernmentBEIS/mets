package uk.gov.pmrv.api.bulkdownload.accountfileattachment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
class Bdrs2BulkDownloadGenerateFileServiceTest {

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @InjectMocks
    private Bdrs2BulkDownloadGenerateFileService bdrs2BulkDownloadGenerateFileService;

    @Test
    void generateFile_createsCorrectZipStructureWithAllFileTypes() throws Exception {
        AccountFileAttachmentDTO bdrDto = mock(AccountFileAttachmentDTO.class);
        when(bdrDto.getWorkflowSubtype()).thenReturn(AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT);
        when(bdrDto.getFileUuid()).thenReturn("UUID_BDR");

        AccountFileAttachmentDTO vosDto = mock(AccountFileAttachmentDTO.class);
        when(vosDto.getWorkflowSubtype()).thenReturn(AccountFileAttachmentWorkflowSubType.BDRS2_VOS);
        when(vosDto.getFileUuid()).thenReturn("UUID_VOS");
        when(vosDto.getOriginatedRequestId()).thenReturn("BDRS2-12345-2026");

        AccountFileAttachmentDTO mmpDto = mock(AccountFileAttachmentDTO.class);
        when(mmpDto.getWorkflowSubtype()).thenReturn(AccountFileAttachmentWorkflowSubType.BDRS2_MMP);
        when(mmpDto.getFileUuid()).thenReturn("UUID_MMP");
        when(mmpDto.getOriginatedRequestId()).thenReturn("BDRS2-12345-2026");

        when(accountFileAttachmentService.getFilesByWorkflowAndPeriodAndCompetentAuthority(
            AccountFileAttachmentWorkflow.BDRS2, "2026-2030", CompetentAuthorityEnum.ENGLAND
        )).thenReturn(List.of(bdrDto, vosDto, mmpDto));

        FileDTO bdrFile = new FileDTO();
        bdrFile.setFileName("bdrs2-report.xlsx");
        bdrFile.setFileContent("BDR_CONTENT".getBytes());

        FileDTO vosFile = new FileDTO();
        vosFile.setFileName("vos.docx");
        vosFile.setFileContent("VOS_CONTENT".getBytes());

        FileDTO mmpFile = new FileDTO();
        mmpFile.setFileName("mmp.docx");
        mmpFile.setFileContent("MMP_CONTENT".getBytes());

        when(fileAttachmentService.getFileDTO("UUID_BDR")).thenReturn(bdrFile);
        when(fileAttachmentService.getFileDTO("UUID_VOS")).thenReturn(vosFile);
        when(fileAttachmentService.getFileDTO("UUID_MMP")).thenReturn(mmpFile);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        bdrs2BulkDownloadGenerateFileService.generateFile(zipOut, "2026-2030", CompetentAuthorityEnum.ENGLAND);

        zipOut.close();

        assertThat(readZipEntryNames(baos)).containsExactlyInAnyOrder(
            "2026-2030 Stage 2 BDR EA/",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR files/",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR VOS/",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR MMP files/",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR files/bdrs2-report.xlsx",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR VOS/BDRS2-12345-2026-VOS-vos.docx",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR MMP files/BDRS2-12345-2026-MMP-mmp.docx"
        );
    }

    @Test
    void generateFile_emptyFileList_createsOnlyFolderStructure() throws Exception {
        when(accountFileAttachmentService.getFilesByWorkflowAndPeriodAndCompetentAuthority(
            AccountFileAttachmentWorkflow.BDRS2, "2026-2030", CompetentAuthorityEnum.ENGLAND
        )).thenReturn(List.of());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        bdrs2BulkDownloadGenerateFileService.generateFile(zipOut, "2026-2030", CompetentAuthorityEnum.ENGLAND);

        zipOut.close();

        assertThat(readZipEntryNames(baos)).containsExactlyInAnyOrder(
            "2026-2030 Stage 2 BDR EA/",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR files/",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR VOS/",
            "2026-2030 Stage 2 BDR EA/Stage 2 BDR MMP files/"
        );
    }

    @Test
    void generateFile_unknownWorkflowSubtype_throwsIllegalArgumentException() {
        AccountFileAttachmentDTO invalidDto = mock(AccountFileAttachmentDTO.class);
        when(invalidDto.getWorkflowSubtype()).thenReturn(AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT);
        when(invalidDto.getFileUuid()).thenReturn("UUID");

        FileDTO file = new FileDTO();
        file.setFileName("filename.pdf");
        file.setFileContent("content".getBytes());
        when(fileAttachmentService.getFileDTO("UUID")).thenReturn(file);

        when(accountFileAttachmentService.getFilesByWorkflowAndPeriodAndCompetentAuthority(
            AccountFileAttachmentWorkflow.BDRS2, "2026-2030", CompetentAuthorityEnum.ENGLAND
        )).thenReturn(List.of(invalidDto));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        assertThatThrownBy(() ->
            bdrs2BulkDownloadGenerateFileService.generateFile(zipOut, "2026-2030", CompetentAuthorityEnum.ENGLAND)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Unknown BDRS2 workflow subtype");
    }

    private List<String> readZipEntryNames(ByteArrayOutputStream baos) throws Exception {
        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
        List<String> names = new ArrayList<>();
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
            names.add(entry.getName());
        }
        return names;
    }
}
