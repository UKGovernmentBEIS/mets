package uk.gov.pmrv.api.workflow.request.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.note.NotePayload;
import uk.gov.netz.api.common.note.NoteRequest;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.notes.service.FileNoteService;
import uk.gov.netz.api.files.notes.service.FileNoteTokenService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestNote;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteDto;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteResponse;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestNoteRepository;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestNoteMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        final AppUser appUser = AppUser.builder().userId("userId").firstName("John").lastName("Jones").build();
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

        requestNoteService.createNote(appUser, requestNoteRequest);

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
    void updateNote() {
        final AppUser appUser = AppUser.builder().userId("new id").firstName("New").lastName("Reg").build();
        
        final long noteId = 2L;
        
        final UUID oldFile = UUID.randomUUID();
        final Set<UUID> oldFiles = Set.of(oldFile);
        final Map<UUID, String> oldFileNames = Map.of(oldFile, "old file name");
        final RequestNote requestNote = RequestNote.builder()
                .id(noteId)
                .requestId("reqId")
                .payload(NotePayload.builder().note("the old note").files(oldFileNames).build())
                .submitterId("old id")
                .submitter("Old Reg")
                .build();

        
        final UUID newFile = UUID.randomUUID();
        final UUID newFileMissingFromDB = UUID.randomUUID();
        final Set<UUID> newFiles = Set.of(newFile, newFileMissingFromDB);
        final Map<UUID, String> newFileNameFetched = Map.of(newFile, "new file name");

        final NoteRequest noteRequest = NoteRequest.builder()
            .note("the new note")
            .files(newFiles)
            .build();

        when(fileNoteService.getFileNames(newFiles)).thenReturn(newFileNameFetched);
        when(requestNoteRepository.findById(noteId)).thenReturn(Optional.of(requestNote));

        requestNoteService.updateNote(noteId, noteRequest, appUser);

        verify(fileNoteService, times(1)).getFileNames(newFiles);
        verify(fileNoteService, times(1)).deleteFiles(oldFiles);
        verify(fileNoteService, times(1)).submitFiles(newFiles);

        assertEquals("the new note", requestNote.getPayload().getNote());
        assertEquals(requestNote.getPayload().getFiles(), newFileNameFetched);
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
