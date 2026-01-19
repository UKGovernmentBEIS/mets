package uk.gov.pmrv.api.account.fileattachment.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AccountFileAttachmentAttachmentService {

    private final FileAttachmentTokenService fileAttachmentTokenService;
    private final AccountFileAttachmentService accountFileAttachmentService;

    public FileToken generateGetFileAttachmentToken(Long accountId, String attachmentUuid) {

        Optional<AccountFileAttachmentDTO> fileDTO = accountFileAttachmentService.getFileByFileUuid(attachmentUuid);
        if (fileDTO.isEmpty() || !Objects.equals(fileDTO.get().getAccountId(), accountId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, attachmentUuid);
        }

        return fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid);
    }
}
