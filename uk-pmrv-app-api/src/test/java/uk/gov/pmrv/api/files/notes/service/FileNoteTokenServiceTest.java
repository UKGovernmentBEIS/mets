package uk.gov.pmrv.api.files.notes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.notes.domain.FileNote;
import uk.gov.pmrv.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.token.FileToken;
import uk.gov.pmrv.api.token.UserFileTokenService;

@ExtendWith(MockitoExtension.class)
class FileNoteTokenServiceTest {

    @InjectMocks
    private FileNoteTokenService service;

    @Mock
    private FileNoteRepository fileNoteRepository;

    @Mock
    private UserFileTokenService userFileTokenService;

    @Test
    void generateGetAccountFileNoteToken_whenExists_thenOk() {
        
        final UUID uuid = UUID.randomUUID();
        final FileToken expectedFileToken = FileToken.builder()
            .token("token")
            .tokenExpirationMinutes(10L)
            .build();
        final Long accountId = 1L;
        final String uuidToString = uuid.toString();
        
        when(fileNoteRepository.existsByAccountIdAndUuid(accountId, uuidToString)).thenReturn(true);
        when(userFileTokenService.generateGetFileToken(uuidToString)).thenReturn(expectedFileToken);

        final FileToken result = service.generateGetAccountFileNoteToken(accountId, uuid);
        
        assertEquals(expectedFileToken, result);
        verify(userFileTokenService, times(1)).generateGetFileToken(uuidToString);
        verify(fileNoteRepository, times(1)).existsByAccountIdAndUuid(accountId, uuidToString);
    }
    
    @Test
    void generateGetAccountFileNoteToken_whenNotExists_thenException() {

        final UUID uuid = UUID.randomUUID();
        final Long accountId = 1L;
        final String uuidToString = uuid.toString();

        when(fileNoteRepository.existsByAccountIdAndUuid(accountId, uuidToString)).thenReturn(false);
        
        final BusinessException be = assertThrows(BusinessException.class,
            () -> service.generateGetAccountFileNoteToken(accountId, uuid));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());

        verify(fileNoteRepository, times(1)).existsByAccountIdAndUuid(accountId, uuidToString);
    }

    @Test
    void generateGetRequestFileNoteToken_whenExists_thenOk() {

        final UUID uuid = UUID.randomUUID();
        final FileToken expectedFileToken = FileToken.builder()
            .token("token")
            .tokenExpirationMinutes(10L)
            .build();
        final String requestId = "requestId";
        final String uuidToString = uuid.toString();

        when(fileNoteRepository.existsByRequestIdAndUuid(requestId, uuidToString)).thenReturn(true);
        when(userFileTokenService.generateGetFileToken(uuidToString)).thenReturn(expectedFileToken);

        final FileToken result = service.generateGetRequestFileNoteToken(requestId, uuid);

        assertEquals(expectedFileToken, result);
        verify(userFileTokenService, times(1)).generateGetFileToken(uuidToString);
        verify(fileNoteRepository, times(1)).existsByRequestIdAndUuid(requestId, uuidToString);
    }

    @Test
    void generateGetRequestFileNoteToken_whenNotExists_thenException() {

        final UUID uuid = UUID.randomUUID();
        final String requestId = "requestId";
        final String uuidToString = uuid.toString();

        when(fileNoteRepository.existsByRequestIdAndUuid(requestId, uuidToString)).thenReturn(false);

        final BusinessException be = assertThrows(BusinessException.class,
            () -> service.generateGetRequestFileNoteToken(requestId, uuid));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());

        verify(fileNoteRepository, times(1)).existsByRequestIdAndUuid(requestId, uuidToString);
    }

    @Test
    void getFileDTOByToken() {
        
        final String token = "token";
        final String fileUuid = "fileUuid";
        final String filename = "filename";
        final FileNote fileNote = FileNote.builder()
            .fileName(filename)
            .fileContent(filename.getBytes())
            .fileSize(filename.length())
            .fileType("docsx")
            .build();

        final FileDTO expectedFileDTO = FileDTO.builder()
            .fileName(fileNote.getFileName())
            .fileContent(fileNote.getFileContent())
            .fileSize(fileNote.getFileSize())
            .fileType(fileNote.getFileType())
            .build();

        when(userFileTokenService.resolveGetFileUuid(token)).thenReturn(fileUuid);
        when(fileNoteRepository.findByUuid(fileUuid)).thenReturn(Optional.of(fileNote));

        final FileDTO result = service.getFileDTOByToken(token);
        assertEquals(expectedFileDTO, result);

        verify(userFileTokenService, times(1)).resolveGetFileUuid(token);
        verify(fileNoteRepository, times(1)).findByUuid(fileUuid);
    }
}