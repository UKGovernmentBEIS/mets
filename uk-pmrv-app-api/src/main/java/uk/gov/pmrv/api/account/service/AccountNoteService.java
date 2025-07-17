package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.AccountNoteAuthorityInfoProvider;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.note.NotePayload;
import uk.gov.netz.api.common.note.NoteRequest;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.notes.service.FileNoteService;
import uk.gov.netz.api.files.notes.service.FileNoteTokenService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.account.domain.AccountNote;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteDto;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteRequest;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteResponse;
import uk.gov.pmrv.api.account.repository.AccountNoteRepository;
import uk.gov.pmrv.api.account.transform.AccountNoteMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountNoteService implements AccountNoteAuthorityInfoProvider {

    private final AccountNoteRepository accountNoteRepository;
    private final AccountNoteMapper accountNoteMapper;
    private final FileNoteService fileNoteService;
    private final FileNoteTokenService fileNoteTokenService;
    private final DateService dateService;
    
    public AccountNoteResponse getAccountNotesByAccountId(final Long accountId,
                                                          final Integer page,
                                                          final Integer pageSize) {
        this.cleanUpUnusedNoteFilesAsync();

        final Page<AccountNote> accountNotePage = accountNoteRepository
            .findAccountNotesByAccountIdOrderByLastUpdatedOnDesc(PageRequest.of(page, pageSize), accountId);
        final List<AccountNoteDto> accountNoteDtos =
            accountNotePage.get().map(accountNoteMapper::toAccountNoteDTO).toList();
        final long totalItems = accountNotePage.getTotalElements();
        
        return AccountNoteResponse.builder().accountNotes(accountNoteDtos).totalItems(totalItems).build();
    }

    public AccountNoteDto getNote(final Long id) {
        
        return accountNoteRepository.findById(id).map(accountNoteMapper::toAccountNoteDTO)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
    
    @Transactional
    public void createNote(final AppUser authUser, final AccountNoteRequest accountNoteRequest) {
        
        final AccountNote accountNote = this.buildAccountNote(accountNoteRequest, authUser);
        accountNoteRepository.save(accountNote);

        final Set<UUID> filesUuids = accountNoteRequest.getFiles();
        fileNoteService.submitFiles(filesUuids);
    }

    @Transactional
    public void updateNote(final Long noteId, final NoteRequest noteRequest, final AppUser authUser) {

        final AccountNote accountNote = accountNoteRepository.findById(noteId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        final Set<UUID> previousFiles = accountNote.getPayload().getFiles().keySet();
        final Set<UUID> currentFiles = noteRequest.getFiles();
        final HashSet<UUID> deletedFiles = new HashSet<>(previousFiles);
        deletedFiles.removeAll(currentFiles);
        if (!deletedFiles.isEmpty()) {
            fileNoteService.deleteFiles(deletedFiles);
        }
        
        final Map<UUID, String> currentFileUuidsWithNames = this.getFileUuidWithNames(currentFiles);

        accountNote.getPayload().setNote(noteRequest.getNote());
        accountNote.getPayload().setFiles(currentFileUuidsWithNames);
        accountNote.setSubmitterId(authUser.getUserId());
        accountNote.setSubmitter(authUser.getFirstName() + " " + authUser.getLastName());
        accountNote.setLastUpdatedOn(dateService.getLocalDateTime());
        fileNoteService.submitFiles(currentFiles);
    }

    @Transactional
    public void deleteNote(final Long noteId) {

        final AccountNote accountNote = accountNoteRepository.findById(noteId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        accountNoteRepository.deleteById(noteId);
        
        final Set<UUID> files = accountNote.getPayload().getFiles().keySet();
        if (!files.isEmpty()) {
            fileNoteService.deleteFiles(files);
        }
    }

    @Override
    public Long getAccountIdById(final Long id) {
        return accountNoteRepository.getAccountIdById(id).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public FileToken generateGetFileNoteToken(final Long accountId, final UUID fileUuid) {
        return fileNoteTokenService.generateGetAccountFileNoteToken(accountId, fileUuid);
    }

    private void cleanUpUnusedNoteFilesAsync() {

        CompletableFuture.runAsync(fileNoteService::cleanUpUnusedFiles)
            .exceptionally(ex -> {
                log.error(ex);
                return null;
            });
    }

    private AccountNote buildAccountNote(final AccountNoteRequest accountNoteRequest, final AppUser authUser) {

        final Map<UUID, String> fileUuidWithNames = this.getFileUuidWithNames(accountNoteRequest.getFiles());

        return AccountNote.builder()
            .accountId(accountNoteRequest.getAccountId())
            .payload(NotePayload.builder()
                .note(accountNoteRequest.getNote())
                .files(fileUuidWithNames)
                .build())
            .submitterId(authUser.getUserId())
            .submitter(authUser.getFirstName() + " " + authUser.getLastName())
            .lastUpdatedOn(dateService.getLocalDateTime())
            .build();
    }

    private Map<UUID, String> getFileUuidWithNames(final Set<UUID> filesUuids) {
        final Map<UUID, String> fileUuidWithNames = fileNoteService.getFileNames(filesUuids);
        
        filesUuids.forEach(uuid -> {
        	if(fileUuidWithNames.get(uuid) == null) {
        		log.warn("Account file note not found for uuid: " + uuid);
        	}
        });
        
        return fileUuidWithNames;
    }
}
