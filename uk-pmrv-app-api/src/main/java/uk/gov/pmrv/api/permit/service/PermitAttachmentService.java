package uk.gov.pmrv.api.permit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.permit.domain.PermitContainer;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PermitAttachmentService {

    private final PermitQueryService permitQueryService;
    private final FileAttachmentTokenService fileAttachmentTokenService;

    public FileToken generateGetFileAttachmentToken(String permitId, UUID attachmentUuid) {
        PermitContainer permitContainer = permitQueryService.getPermitContainerById(permitId);

        //validate
        if (!permitContainer.getPermitAttachments().containsKey(attachmentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, attachmentUuid);
        }

        return fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString());
    }

}
