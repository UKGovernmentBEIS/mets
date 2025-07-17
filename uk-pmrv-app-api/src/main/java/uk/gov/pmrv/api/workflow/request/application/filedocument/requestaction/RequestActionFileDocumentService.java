package uk.gov.pmrv.api.workflow.request.application.filedocument.requestaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestActionFileDocumentService {

    private final RequestActionRepository requestActionRepository;
    private final FileDocumentTokenService fileDocumentTokenService;
    
    @Transactional
    public FileToken generateGetFileDocumentToken(Long requestActionId, UUID fileDocumentUuid) {
        RequestAction requestAction = requestActionRepository.findById(requestActionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if(!requestAction.getPayload().getFileDocuments().containsKey(fileDocumentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, fileDocumentUuid);
        }

        return fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString());
    }
}
