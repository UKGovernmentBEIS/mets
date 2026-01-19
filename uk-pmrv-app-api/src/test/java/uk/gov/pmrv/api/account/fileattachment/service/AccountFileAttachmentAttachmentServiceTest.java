package uk.gov.pmrv.api.account.fileattachment.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class AccountFileAttachmentAttachmentServiceTest {

    @InjectMocks
    private AccountFileAttachmentAttachmentService service;

    @Mock
    private FileAttachmentTokenService fileAttachmentTokenService;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @Test
    void generateGetFileAttachmentToken_success() {
        // Given
        Long accountId = 1L;
        String attachmentUuid = "UUID123";

        AccountFileAttachmentDTO fileDTO = AccountFileAttachmentDTO.builder()
                .fileUuid(attachmentUuid)
                .accountId(accountId)
                .build();

        FileToken expectedToken = new FileToken("tokenValue", 1L);

        when(accountFileAttachmentService.getFileByFileUuid(attachmentUuid))
                .thenReturn(Optional.of(fileDTO));
        when(fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid))
                .thenReturn(expectedToken);

        // When
        FileToken result = service.generateGetFileAttachmentToken(accountId, attachmentUuid);

        // Then
        assertThat(result).isEqualTo(expectedToken);

        verify(accountFileAttachmentService).getFileByFileUuid(attachmentUuid);
        verify(fileAttachmentTokenService).generateGetFileAttachmentToken(attachmentUuid);
    }

    @Test
    void generateGetFileAttachmentToken_fileNotFound_throwsException() {
        // Given
        Long accountId = 1L;
        String attachmentUuid = "UUID123";

        when(accountFileAttachmentService.getFileByFileUuid(attachmentUuid))
                .thenReturn(Optional.empty());

        // When + Then
        assertThatThrownBy(() -> service.generateGetFileAttachmentToken(accountId, attachmentUuid))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountFileAttachmentService).getFileByFileUuid(attachmentUuid);
        verifyNoInteractions(fileAttachmentTokenService);
    }

    @Test
    void generateGetFileAttachmentToken_wrongAccount_throwsException() {
        // Given
        Long accountId = 1L;
        String attachmentUuid = "UUID123";

        AccountFileAttachmentDTO fileDTO = AccountFileAttachmentDTO.builder()
                .fileUuid(attachmentUuid)
                .accountId(2L) // ← different account
                .build();

        when(accountFileAttachmentService.getFileByFileUuid(attachmentUuid))
                .thenReturn(Optional.of(fileDTO));

        // When + Then
        assertThatThrownBy(() -> service.generateGetFileAttachmentToken(accountId, attachmentUuid))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountFileAttachmentService).getFileByFileUuid(attachmentUuid);
        verifyNoInteractions(fileAttachmentTokenService);
    }
}
