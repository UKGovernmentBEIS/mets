package uk.gov.pmrv.api.permit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermitDocumentService {

    private final PermitQueryService permitQueryService;
    private final FileDocumentTokenService fileDocumentTokenService;

    public FileToken generateGetFileDocumentToken(final String permitId,
                                                  final UUID documentUuid) {
        
        // validate permitId, document uuid pair
        permitQueryService.getPermitByIdAndFileDocumentUuid(permitId, documentUuid.toString());
        return fileDocumentTokenService.generateGetFileDocumentToken(documentUuid.toString());
    }
}
