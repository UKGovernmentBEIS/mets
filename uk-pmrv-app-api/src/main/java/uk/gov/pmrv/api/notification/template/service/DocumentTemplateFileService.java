package uk.gov.pmrv.api.notification.template.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentTemplateService;
import uk.gov.netz.api.files.documents.service.FileDocumentTemplateTokenService;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.repository.DocumentTemplateRepository;
import uk.gov.netz.api.token.FileToken;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentTemplateFileService {

    private final DocumentTemplateQueryService documentTemplateQueryService;
    private final DocumentTemplateRepository documentTemplateRepository;
    private final FileDocumentTemplateService fileDocumentTemplateService;
    private final FileDocumentTemplateTokenService fileDocumentTemplateTokenService;

    @Transactional
    public FileToken generateGetFileDocumentTemplateToken(Long documentTemplateId, UUID fileUuid) {
        DocumentTemplate documentTemplate = documentTemplateQueryService.getDocumentTemplateById(documentTemplateId);
        validateFileDocumentTemplate(fileUuid, documentTemplate);
        
        return fileDocumentTemplateTokenService.generateGetFileDocumentTemplateToken(fileUuid.toString());
    }
    
    @Transactional(readOnly = true)
    public FileDTO getFileDocumentTemplateByTypeAndCompetentAuthorityAndAccountType(DocumentTemplateType type,
                                                                                    CompetentAuthorityEnum competentAuthority,
                                                                                    AccountType accountType) {
        DocumentTemplate documentTemplate = documentTemplateRepository
            .findByTypeAndCompetentAuthorityAndAccountType(type, competentAuthority, accountType)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        return fileDocumentTemplateService.getFileDocumentTemplateById(documentTemplate.getFileDocumentTemplateId());
    }

    private void validateFileDocumentTemplate(UUID fileUuid, DocumentTemplate documentTemplate) {
        final FileInfoDTO fileDocumentTemplate = fileDocumentTemplateService.getFileInfoDocumentTemplateById(documentTemplate.getFileDocumentTemplateId());
        
        if(!fileDocumentTemplate.getUuid().equals(fileUuid.toString())) {
            throw new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_NOT_FOUND);
        }
    }
}
