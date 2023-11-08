package uk.gov.pmrv.api.workflow.request.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.note.NotePayload;
import uk.gov.pmrv.api.common.note.NoteRequest;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.notes.service.FileNoteService;
import uk.gov.pmrv.api.files.notes.service.FileNoteTokenService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestNote;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteDto;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteResponse;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestNoteRepository;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestNoteMapper;

@ExtendWith(MockitoExtension.class)
class RequestNoteServiceTest {

    @InjectMocks
    private RequestNoteService requestNoteService;

    @Mock
    private RequestNoteRepository requestNoteRepository;

    @Mock
    private RequestNoteMapper requestNoteMapper;

    @Mock
    private FileNoteService fileNoteService;

    @Mock
    private FileNoteTokenService fileNoteTokenService;

    @Mock
    private DateService dateService;

    @Test
    void getRequestNotesByRequestId() {

        final String requestId = "requestId";

        final NotePayload notePayload1 = NotePayload.builder().note("note 1").build();
        final NotePayload notePayload2 = NotePayload.builder().note("note 2").build();
        
        final RequestNote requestNote1 = RequestNote.builder().requestId(requestId).payload(notePayload1).build();
        final RequestNote requestNote2 = RequestNote.builder().requestId(requestId).payload(notePayload2).build();
        final List<RequestNote> requestNotes = List.of(requestNote1, requestNote2);

        final RequestNoteDto requestNoteDto1 = RequestNoteDto.builder().requestId(requestId).payload(notePayload1).build();
        final RequestNoteDto requestNoteDto2 = RequestNoteDto.builder().requestId(requestId).payload(notePayload2).build();
        final List<RequestNoteDto> requestNoteDtos = List.of(requestNoteDto1, requestNoteDto2);
        
        final Page<RequestNote> pagedRequestNoteDtos = new PageImpl<>(requestNotes, PageRequest.of(1, 5), 15);
        
        when(requestNoteRepository.findRequestNotesByRequestIdOrderByLastUpdatedOnDesc(PageRequest.of(1, 5), requestId))
            .thenReturn(pagedRequestNoteDtos);
        when(requestNoteMapper.toRequestNoteDTO(requestNote1)).thenReturn(requestNoteDto1);
        when(requestNoteMapper.toRequestNoteDTO(requestNote2)).thenReturn(requestNoteDto2);
        
        final RequestNoteResponse actualResult = requestNoteService.getRequestNotesByRequestId(requestId, 1, 5);

        final RequestNoteResponse expectedResult = RequestNoteResponse.builder().requestNotes(requestNoteDtos).totalItems(15L).build();
        
        assertThat(actualResult).isEqualTo(expectedResult);

        verify(requestNoteMapper, times(1)).toRequestNoteDTO(requestNote1);
        verify(requestNoteMapper, times(1)).toRequestNoteDTO(requestNote2);
        verify(requestNoteRepository, times(1)).findRequestNotesByRequestIdOrderByLastUpdatedOnDesc(PageRequest.of(1, 5), requestId);
        verify(fileNoteService, timeout(2000).times(1)).cleanUpUnusedFiles();
    }

    @Test
    void getNote() {

        final long noteId = 2L;
        final RequestNote requestNote = RequestNote.builder()
            .payload(NotePayload.builder()
                .note("the note")
                .build())
            .build();
        final RequestNoteDto requestNoteDto = RequestNoteDto.builder()
            .payload(NotePayload.builder()
                .note("the note")
                .build())
            .build();

        when(requestNoteRepository.findById(noteId)).thenReturn(Optional.of(requestNote));
        when(requestNoteMapper.toRequestNoteDTO(requestNote)).thenReturn(requestNoteDto);

        requestNoteService.getNote(noteId);

        verify(requestNoteRepository, times(1)).findById(noteId);
    }

    @Test
    void createNote_whenFilesExist_thenOK() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").firstName("John").lastName("Jones").build();
        final UUID file = UUID.randomUUID();
        final Set<UUID> files = Set.of(file);
        final RequestNoteRequest requestNoteRequest = RequestNoteRequest.builder()
            .requestId("reqId")
            .note("the note")
            .files(files)
            .build();
        final LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 1, 1);

        when(fileNoteService.getFileNames(files)).thenReturn(Map.of(file, "file name"));
        when(dateService.getLocalDateTime()).thenReturn(dateTime);

        requestNoteService.createNote(pmrvUser, requestNoteRequest);

        final RequestNote requestNote = RequestNote.builder()
            .requestId("reqId")
            .payload(NotePayload.builder().note("the note").files(Map.of(file, "file name")).build())
            .submitterId("userId")
            .submitter("John Jones")
            .lastUpdatedOn(dateTime)
            .build();

        verify(fileNoteService, times(1)).getFileNames(files);
        verify(fileNoteService, times(1)).submitFiles(files);
        verify(requestNoteRepository, times(1)).save(requestNote);
    }

    @Test
    void createNote_whenFilesDoNotExist_thenException() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").firstName("John").lastName("Jones").build();
        final UUID file = UUID.randomUUID();
        final Set<UUID> files = Set.of(file);
        final RequestNoteRequest requestNoteRequest = RequestNoteRequest.builder()
            .requestId("reqId")
            .note("the note")
            .files(files)
            .build();

        when(fileNoteService.getFileNames(files)).thenReturn(Map.of());

        assertThrows(BusinessException.class, () -> requestNoteService.createNote(pmrvUser, requestNoteRequest));

        verify(fileNoteService, times(1)).getFileNames(files);
        verify(fileNoteService, never()).submitFiles(files);
        verify(requestNoteRepository, never()).save(any());
    }

    @Test
    void updateNote_whenFilesExist_thenOK() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("new id").firstName("New").lastName("Reg").build();
        final UUID file = UUID.randomUUID();
        final Set<UUID> oldFiles = Set.of(file);
        final UUID newFile = UUID.randomUUID();
        final Set<UUID> newFiles = Set.of(newFile);

        final NoteRequest noteRequest = NoteRequest.builder()
            .note("the new note")
            .files(newFiles)
            .build();

        final long noteId = 2L;

        final Map<UUID, String> oldFileName = Map.of(file, "old file name");
        final Map<UUID, String> newFileName = Map.of(file, "new file name");

        final RequestNote requestNote = RequestNote.builder()
            .id(noteId)
            .requestId("reqId")
            .payload(NotePayload.builder().note("the old note").files(oldFileName).build())
            .submitterId("old id")
            .submitter("Old Reg")
            .build();

        when(fileNoteService.getFileNames(newFiles)).thenReturn(newFileName);
        when(requestNoteRepository.findById(noteId)).thenReturn(Optional.of(requestNote));

        requestNoteService.updateNote(noteId, noteRequest, pmrvUser);

        verify(fileNoteService, times(1)).getFileNames(newFiles);
        verify(fileNoteService, times(1)).deleteFiles(oldFiles);

        assertEquals("the new note", requestNote.getPayload().getNote());
        assertEquals(requestNote.getPayload().getFiles(), newFileName);
        assertEquals("new id", requestNote.getSubmitterId());
        assertEquals("New Reg", requestNote.getSubmitter());
    }

    @Test
    void deleteNode() {

        final long noteId = 2L;
        final UUID file = UUID.randomUUID();
        final RequestNote requestNote = RequestNote.builder()
            .payload(NotePayload.builder()
                .files(Map.of(file, "filename"))
                .build())
            .build();

        when(requestNoteRepository.findById(noteId)).thenReturn(Optional.of(requestNote));

        requestNoteService.deleteNote(noteId);

        verify(requestNoteRepository, times(1)).deleteById(noteId);
        verify(fileNoteService, times(1)).deleteFiles(Set.of(file));
    }

    @Test
    void generateGetFileNoteToken() {

        final String requestId = "requestId";
        final UUID file = UUID.randomUUID();

        requestNoteService.generateGetFileNoteToken(requestId, file);
        
        verify(fileNoteTokenService, times(1)).generateGetRequestFileNoteToken(requestId, file);
    }
}
