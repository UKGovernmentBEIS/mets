package uk.gov.pmrv.api.account.service;

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
import uk.gov.pmrv.api.account.domain.AccountNote;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteDto;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteRequest;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteResponse;
import uk.gov.pmrv.api.account.repository.AccountNoteRepository;
import uk.gov.pmrv.api.account.transform.AccountNoteMapper;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.note.NotePayload;
import uk.gov.pmrv.api.common.note.NoteRequest;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.notes.service.FileNoteService;
import uk.gov.pmrv.api.files.notes.service.FileNoteTokenService;

@ExtendWith(MockitoExtension.class)
class AccountNoteServiceTest {

    @InjectMocks
    private AccountNoteService accountNoteService;

    @Mock
    private AccountNoteRepository accountNoteRepository;

    @Mock
    private AccountNoteMapper accountNoteMapper;

    @Mock
    private FileNoteService fileNoteService;
    
    @Mock
    private FileNoteTokenService fileNoteTokenService;
    
    @Mock
    private DateService dateService;

    @Test
    void getAccountNotesByAccountId() {

        final Long accountId = 1L;

        final NotePayload notePayload1 = NotePayload.builder().note("note 1").build();
        final NotePayload notePayload2 = NotePayload.builder().note("note 2").build();
        
        final AccountNote accountNote1 = AccountNote.builder().accountId(accountId).payload(notePayload1).build();
        final AccountNote accountNote2 = AccountNote.builder().accountId(accountId).payload(notePayload2).build();
        final List<AccountNote> accountNotes = List.of(accountNote1, accountNote2);

        final AccountNoteDto accountNoteDto1 = AccountNoteDto.builder().accountId(accountId).payload(notePayload1).build();
        final AccountNoteDto accountNoteDto2 = AccountNoteDto.builder().accountId(accountId).payload(notePayload2).build();
        final List<AccountNoteDto> accountNoteDtos = List.of(accountNoteDto1, accountNoteDto2);
        
        final Page<AccountNote> pagedAccountNoteDtos = new PageImpl<>(accountNotes, PageRequest.of(1, 5), 15);
        
        when(accountNoteRepository.findAccountNotesByAccountIdOrderByLastUpdatedOnDesc(PageRequest.of(1, 5), accountId))
            .thenReturn(pagedAccountNoteDtos);
        when(accountNoteMapper.toAccountNoteDTO(accountNote1)).thenReturn(accountNoteDto1);
        when(accountNoteMapper.toAccountNoteDTO(accountNote2)).thenReturn(accountNoteDto2);
        
        final AccountNoteResponse actualResult = accountNoteService.getAccountNotesByAccountId(accountId, 1, 5);

        final AccountNoteResponse expectedResult = AccountNoteResponse.builder().accountNotes(accountNoteDtos).totalItems(15L).build();
        
        assertThat(actualResult).isEqualTo(expectedResult);

        verify(accountNoteMapper, times(1)).toAccountNoteDTO(accountNote1);
        verify(accountNoteMapper, times(1)).toAccountNoteDTO(accountNote2);
        verify(accountNoteRepository, times(1)).findAccountNotesByAccountIdOrderByLastUpdatedOnDesc(PageRequest.of(1, 5), accountId);
        verify(fileNoteService, timeout(2000).times(1)).cleanUpUnusedFiles();
    }

    @Test
    void getNote() {

        final long noteId = 2L;
        final AccountNote accountNote = AccountNote.builder()
            .payload(NotePayload.builder()
                .note("the note")
                .build())
            .build();
        final AccountNoteDto accountNoteDto = AccountNoteDto.builder()
            .payload(NotePayload.builder()
                .note("the note")
                .build())
            .build();

        when(accountNoteRepository.findById(noteId)).thenReturn(Optional.of(accountNote));
        when(accountNoteMapper.toAccountNoteDTO(accountNote)).thenReturn(accountNoteDto);

        accountNoteService.getNote(noteId);

        verify(accountNoteRepository, times(1)).findById(noteId);
    }
    
    @Test
    void createNote_whenFilesExist_thenOK() {
        
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").firstName("John").lastName("Jones").build();
        final UUID file = UUID.randomUUID();
        final Set<UUID> files = Set.of(file);
        final AccountNoteRequest accountNoteRequest = AccountNoteRequest.builder()
            .accountId(1L)
            .note("the note")
            .files(files)
            .build();
        final LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 1, 1);
        
        when(fileNoteService.getFileNames(files)).thenReturn(Map.of(file, "file name"));
        when(dateService.getLocalDateTime()).thenReturn(dateTime);
        
        accountNoteService.createNote(pmrvUser, accountNoteRequest);

        final AccountNote accountNote = AccountNote.builder()
            .accountId(1L)
            .payload(NotePayload.builder().note("the note").files(Map.of(file, "file name")).build())
            .submitterId("userId")
            .submitter("John Jones")
            .lastUpdatedOn(dateTime)
            .build();

        verify(fileNoteService, times(1)).getFileNames(files);
        verify(fileNoteService, times(1)).submitFiles(files);
        verify(accountNoteRepository, times(1)).save(accountNote);
    }
    
    @Test
    void createNote_whenFilesDoNotExist_thenException() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").firstName("John").lastName("Jones").build();
        final UUID file = UUID.randomUUID();
        final Set<UUID> files = Set.of(file);
        final AccountNoteRequest accountNoteRequest = AccountNoteRequest.builder()
            .accountId(1L)
            .note("the note")
            .files(files)
            .build();

        when(fileNoteService.getFileNames(files)).thenReturn(Map.of());

        assertThrows(BusinessException.class, () -> accountNoteService.createNote(pmrvUser, accountNoteRequest));

        verify(fileNoteService, times(1)).getFileNames(files);
        verify(fileNoteService, never()).submitFiles(files);
        verify(accountNoteRepository, never()).save(any());
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
        final long accountId = 1L;

        final Map<UUID, String> oldFileName = Map.of(file, "old file name");
        final Map<UUID, String> newFileName = Map.of(file, "new file name");
        
        final AccountNote accountNote = AccountNote.builder()
            .id(noteId)
            .accountId(accountId)
            .payload(NotePayload.builder().note("the old note").files(oldFileName).build())
            .submitterId("old id")
            .submitter("Old Reg")
            .build();
        
        when(fileNoteService.getFileNames(newFiles)).thenReturn(newFileName);
        when(accountNoteRepository.findById(noteId)).thenReturn(Optional.of(accountNote));

        accountNoteService.updateNote(noteId, noteRequest, pmrvUser);

        verify(fileNoteService, times(1)).getFileNames(newFiles);
        verify(fileNoteService, times(1)).deleteFiles(oldFiles);

        assertEquals("the new note", accountNote.getPayload().getNote());
        assertEquals(accountNote.getPayload().getFiles(), newFileName);
        assertEquals("new id", accountNote.getSubmitterId());
        assertEquals("New Reg", accountNote.getSubmitter());
    }
    
    @Test
    void deleteNode() {

        final long noteId = 2L;
        final UUID file = UUID.randomUUID();
        final AccountNote accountNote = AccountNote.builder()
            .payload(NotePayload.builder()
                .files(Map.of(file, "filename"))
                .build())
            .build();

        when(accountNoteRepository.findById(noteId)).thenReturn(Optional.of(accountNote));

        accountNoteService.deleteNote(noteId);

        verify(accountNoteRepository, times(1)).deleteById(noteId);
        verify(fileNoteService, times(1)).deleteFiles(Set.of(file));
    }
    
    @Test
    void generateGetFileNoteToken() {

        final long accountId = 2L;
        final UUID file = UUID.randomUUID();
        
        accountNoteService.generateGetFileNoteToken(accountId, file);
        
        verify(fileNoteTokenService, times(1)).generateGetAccountFileNoteToken(accountId, file);
    }
}
