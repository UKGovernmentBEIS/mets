package uk.gov.pmrv.api.notification.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@RequiredArgsConstructor
public class DocumentFileGeneratorService {
    
    private final DocumentTemplateFileService documentTemplateFileService;
    private final DocumentTemplateProcessService documentTemplateProcessService;
    private final FileDocumentService fileDocumentService;
    
    @Transactional
    public FileInfoDTO generateAndSaveFileDocument(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        
        final byte[] generatedFile = this.generateFileBytes(type, templateParams, fileNameToGenerate);
        return fileDocumentService.createFileDocument(generatedFile, fileNameToGenerate);
    }
    
    @Transactional
    public CompletableFuture<FileInfoDTO> generateAndSaveFileDocumentAsync(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        
        return CompletableFuture.supplyAsync(() -> {
                final byte[] generatedFile = this.generateFileBytes(type, templateParams, fileNameToGenerate);
                return fileDocumentService.createFileDocument(generatedFile, fileNameToGenerate);
            }
        ); 
    }

    @Transactional(readOnly = true)
    public FileDTO generateFileDocument(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        
        final byte[] generatedFile = this.generateFileBytes(type, templateParams, fileNameToGenerate);
        return FileDTO.builder()
            .fileContent(generatedFile)
            .fileName(fileNameToGenerate)
            .fileType(MimeTypeUtils.detect(generatedFile, fileNameToGenerate))
            .fileSize(generatedFile.length)
            .build();
    }
    
    private byte[] generateFileBytes(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        //get file document template
        final FileDTO fileDocumentTemplate = documentTemplateFileService
            .getFileDocumentTemplateByTypeAndCompetentAuthorityAndAccountType(
                type,
                templateParams.getCompetentAuthorityParams().getCompetentAuthority().getId(),
                templateParams.getAccountParams().getAccountType()
            );

        //generate file from template
    	final byte[] generatedFile;
    	try {
            generatedFile = documentTemplateProcessService.generateFileDocumentFromTemplate(
                    fileDocumentTemplate, templateParams,fileNameToGenerate);
        } catch (DocumentTemplateProcessException e) {
            log.error(String.format(
                    "Document template file generation failed for template: %s, requestId: %s, account name: %s",
                    fileDocumentTemplate.getFileName(),
                    templateParams.getWorkflowParams().getRequestId(),
                    templateParams.getAccountParams().getName()), e);
            throw new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, fileDocumentTemplate.getFileName());
        }
        return generatedFile;
    }

}
