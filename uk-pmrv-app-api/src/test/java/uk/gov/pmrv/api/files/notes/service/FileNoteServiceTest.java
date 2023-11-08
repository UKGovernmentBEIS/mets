package uk.gov.pmrv.api.files.notes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.pmrv.api.files.common.service.FileScanValidatorService;
import uk.gov.pmrv.api.files.common.service.FileValidatorService;
import uk.gov.pmrv.api.files.notes.domain.FileNote;
import uk.gov.pmrv.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.files.notes.transform.FileNoteMapper;

@ExtendWith(MockitoExtension.class)
class FileNoteServiceTest {
    
    @InjectMocks
    private FileNoteService fileNoteService;
    
    @Mock
    private FileNoteRepository fileNoteRepository;
    
    @Mock
    private DateService dateService;
    
    @Mock
    private FileNoteMapper fileNoteMapper;

    @Mock
    private FileScanValidatorService fileScanValidator;

    @Spy
    private ArrayList<FileValidatorService> fileValidators;

    @BeforeEach
    void setUp() {
        fileValidators.add(fileScanValidator);
    }

    @Test
    void cleanUpUnusedFiles() {

        final LocalDateTime today = LocalDateTime.of(2022, 1, 2, 3, 4);
        final LocalDateTime yesterday = LocalDateTime.of(2022, 1, 1, 3, 4);
        
        when(dateService.getLocalDateTime()).thenReturn(today);
        
        fileNoteService.cleanUpUnusedFiles();

        verify(dateService, times(1)).getLocalDateTime();
        verify(fileNoteRepository, times(1)).deleteNoteFilesByStatusAndDateBefore(FileStatus.PENDING, yesterday);
    }

    @Test
    void getFileNames() {

        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        final Set<UUID> fileUUids = Set.of(uuid1, uuid2);
        final String uuidString1 = uuid1.toString();
        final String uuidString2 = uuid2.toString();
        final Set<String> fileStrings = Set.of(uuidString1, uuidString2);

        final List<FileInfoDTO> fileInfoDTOS = List.of(
            FileInfoDTO.builder().name("name1").uuid(uuidString1).build(),
            FileInfoDTO.builder().name("name2").uuid(uuidString2).build()
        );

        when(fileNoteRepository.getFileNamesByUuid(fileStrings)).thenReturn(fileInfoDTOS);

        final Map<UUID, String> result = fileNoteService.getFileNames(fileUUids);

        verify(fileNoteRepository, times(1)).getFileNamesByUuid(fileStrings);

        assertEquals(result, Map.of(uuid1, "name1", uuid2, "name2"));
    }

    @Test
    void uploadAccountFile() throws IOException {

        byte[] contentBytes = "dummycontent".getBytes();
        final FileDTO fileDTO = FileDTO.builder()
            .fileName("name")
            .fileSize(contentBytes.length)
            .fileType("application/pdf")
            .fileContent(contentBytes)
            .build();
        final PmrvUser authUser = PmrvUser.builder().userId("user").build();
        final FileStatus status = FileStatus.PENDING;

        final FileNote fileNote = FileNote.builder()
            .fileName("name")
            .fileSize(contentBytes.length)
            .fileType("application/pdf")
            .fileContent(contentBytes)
            .build();
        final Long accountId = 1L;
        
        when(fileNoteMapper.toFileNote(fileDTO)).thenReturn(fileNote);
        
        final FileUuidDTO fileUuidDTO = fileNoteService.uploadAccountFile(authUser, fileDTO, accountId);

        assertThat(fileUuidDTO).isNotNull();
        
        ArgumentCaptor<FileNote> fileCaptor = ArgumentCaptor.forClass(FileNote.class);
        verify(fileNoteRepository, times(1)).save(fileCaptor.capture());
        FileNote fileCaptured = fileCaptor.getValue();
        assertThat(fileCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(fileCaptured.getFileName()).isEqualTo(fileDTO.getFileName());
        assertThat(fileCaptured.getFileSize()).isEqualTo(fileDTO.getFileSize());
        assertThat(fileCaptured.getFileType()).isEqualTo(fileDTO.getFileType());
        assertThat(fileCaptured.getFileContent()).isEqualTo(contentBytes);
        assertThat(fileCaptured.getCreatedBy()).isEqualTo(authUser.getUserId());
        assertThat(fileCaptured.getStatus()).isEqualTo(status);
        assertThat(fileCaptured.getUuid()).isEqualTo(fileUuidDTO.getUuid());

        verify(fileScanValidator, times(1)).validate(fileDTO);
    }

    @Test
    void uploadRequestFile() throws IOException {

        byte[] contentBytes = "dummycontent".getBytes();
        final FileDTO fileDTO = FileDTO.builder()
            .fileName("name")
            .fileSize(contentBytes.length)
            .fileType("application/pdf")
            .fileContent(contentBytes)
            .build();
        final PmrvUser authUser = PmrvUser.builder().userId("user").build();
        final FileStatus status = FileStatus.PENDING;

        final FileNote fileNote = FileNote.builder()
            .fileName("name")
            .fileSize(contentBytes.length)
            .fileType("application/pdf")
            .fileContent(contentBytes)
            .build();
        final String requestId = "requestId";

        when(fileNoteMapper.toFileNote(fileDTO)).thenReturn(fileNote);

        final FileUuidDTO fileUuidDTO = fileNoteService.uploadRequestFile(authUser, fileDTO, requestId);

        assertThat(fileUuidDTO).isNotNull();

        ArgumentCaptor<FileNote> fileCaptor = ArgumentCaptor.forClass(FileNote.class);
        verify(fileNoteRepository, times(1)).save(fileCaptor.capture());
        FileNote fileCaptured = fileCaptor.getValue();
        assertThat(fileCaptured.getRequestId()).isEqualTo(requestId);
        assertThat(fileCaptured.getFileName()).isEqualTo(fileDTO.getFileName());
        assertThat(fileCaptured.getFileSize()).isEqualTo(fileDTO.getFileSize());
        assertThat(fileCaptured.getFileType()).isEqualTo(fileDTO.getFileType());
        assertThat(fileCaptured.getFileContent()).isEqualTo(contentBytes);
        assertThat(fileCaptured.getCreatedBy()).isEqualTo(authUser.getUserId());
        assertThat(fileCaptured.getStatus()).isEqualTo(status);
        assertThat(fileCaptured.getUuid()).isEqualTo(fileUuidDTO.getUuid());

        verify(fileScanValidator, times(1)).validate(fileDTO);
    }

    @Test
    void submitFiles() {

        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        final Set<UUID> fileUUids = Set.of(uuid1, uuid2);
        final String uuidString1 = uuid1.toString();
        final String uuidString2 = uuid2.toString();
        final Set<String> fileStrings = Set.of(uuidString1, uuidString2);

        fileNoteService.submitFiles(fileUUids);

        verify(fileNoteRepository, times(1)).updateNoteFilesStatusByUuid(fileStrings, FileStatus.SUBMITTED);
    }

    @Test
    void deleteFiles() {

        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        final Set<UUID> fileUUids = Set.of(uuid1, uuid2);
        final String uuidString1 = uuid1.toString();
        final String uuidString2 = uuid2.toString();
        final Set<String> fileStrings = Set.of(uuidString1, uuidString2);

        fileNoteService.deleteFiles(fileUUids);

        verify(fileNoteRepository, times(1)).deleteNoteFilesByUuid(fileStrings);
    }
}
