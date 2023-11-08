package uk.gov.pmrv.api.permit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTokenService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.token.FileToken;

@ExtendWith(MockitoExtension.class)
class PermitDocumentServiceTest {

    @InjectMocks
    private PermitDocumentService service;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private FileDocumentTokenService fileDocumentTokenService;

    @Test
    void generateGetFileAttachmentToken() {
        
        final String permitId = "1";
        final UUID documentUuid = UUID.randomUUID();

        final PermitEntityDto permitEntityDto = PermitEntityDto.builder()
            .permitContainer(
                PermitContainer.builder()
                    .permit(Permit.builder().build())
                    .build())
            .fileDocumentUuid(documentUuid.toString())
            .build();

        final FileToken fileToken = FileToken.builder().token("token").build();
        
        when(permitQueryService.getPermitByIdAndFileDocumentUuid(permitId, documentUuid.toString())).thenReturn(permitEntityDto);
        when(fileDocumentTokenService.generateGetFileDocumentToken(documentUuid.toString())).thenReturn(
            fileToken);

        final FileToken result = service.generateGetFileDocumentToken(permitId, documentUuid);

        assertThat(result).isEqualTo(fileToken);
        verify(permitQueryService, times(1)).getPermitByIdAndFileDocumentUuid(permitId, documentUuid.toString());
        verify(fileDocumentTokenService, times(1)).generateGetFileDocumentToken(documentUuid.toString());
    }

    @Test
    void getFileAttachment_uuid_not_found_in_permit() {
        
        final String permitId = "1";
        final UUID documentUuid = UUID.randomUUID();

        when(permitQueryService.getPermitByIdAndFileDocumentUuid(permitId, documentUuid.toString()))
            .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        final BusinessException be = assertThrows(BusinessException.class, () -> service.generateGetFileDocumentToken(permitId, documentUuid));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(permitQueryService, times(1)).getPermitByIdAndFileDocumentUuid(permitId, documentUuid.toString());
        verifyNoInteractions(fileDocumentTokenService);
    }
}
