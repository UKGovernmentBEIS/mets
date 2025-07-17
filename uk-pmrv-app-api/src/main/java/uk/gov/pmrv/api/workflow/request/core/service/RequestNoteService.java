package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.note.NotePayload;
import uk.gov.netz.api.common.note.NoteRequest;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.notes.service.FileNoteService;
import uk.gov.netz.api.files.notes.service.FileNoteTokenService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestNote;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteDto;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteResponse;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestNoteRepository;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestNoteMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class RequestNoteService {

    private final RequestNoteRepository requestNoteRepository;
    private final RequestNoteMapper requestNoteMapper;
    private final FileNoteService fileNoteService;
    private final FileNoteTokenService fileNoteTokenService;
    private final DateService dateService;

    public RequestNoteResponse getRequestNotesByRequestId(final String requestId,
                                                          final Integer page,
                                                          final Integer pageSize) {
        this.cleanUpUnusedNoteFilesAsync();

        final Page<RequestNote> requestNotePage = requestNoteRepository
            .findRequestNotesByRequestIdOrderByLastUpdatedOnDesc(PageRequest.of(page, pageSize), requestId);
        final List<RequestNoteDto> requestNoteDtos =
            requestNotePage.get().map(requestNoteMapper::toRequestNoteDTO).toList();
        final long totalItems = requestNotePage.getTotalElements();

        return RequestNoteResponse.builder().requestNotes(requestNoteDtos).totalItems(totalItems).build();
    }

    public RequestNoteDto getNote(final Long id) {

        return requestNoteRepository.findById(id).map(requestNoteMapper::toRequestNoteDTO)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public void createNote(final AppUser authUser, final RequestNoteRequest requestNoteRequest) {

        final RequestNote requestNote = this.buildRequestNote(requestNoteRequest, authUser);
        requestNoteRepository.save(requestNote);

        final Set<UUID> filesUuids = requestNoteRequest.getFiles();
        fileNoteService.submitFiles(filesUuids);
    }

    @Transactional
    public void updateNote(final Long noteId, final NoteRequest noteRequest, final AppUser authUser) {

        final RequestNote requestNote = requestNoteRepository.findById(noteId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        final Set<UUID> previousFiles = requestNote.getPayload().getFiles().keySet();
        final Set<UUID> currentFiles = noteRequest.getFiles();
        final HashSet<UUID> deletedFiles = new HashSet<>(previousFiles);
        deletedFiles.removeAll(currentFiles);
        if (!deletedFiles.isEmpty()) {
            fileNoteService.deleteFiles(deletedFiles);
        }

        final Map<UUID, String> currentFileUuidsWithNames = this.getFileUuidWithNames(currentFiles);

        requestNote.getPayload().setNote(noteRequest.getNote());
        requestNote.getPayload().setFiles(currentFileUuidsWithNames);
        requestNote.setSubmitterId(authUser.getUserId());
        requestNote.setSubmitter(authUser.getFirstName() + " " + authUser.getLastName());
        requestNote.setLastUpdatedOn(dateService.getLocalDateTime());
        fileNoteService.submitFiles(currentFiles);
    }

    @Transactional
    public void deleteNote(final Long noteId) {

        final RequestNote requestNote = requestNoteRepository.findById(noteId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        requestNoteRepository.deleteById(noteId);

        final Set<UUID> files = requestNote.getPayload().getFiles().keySet();
        if (!files.isEmpty()) {
            fileNoteService.deleteFiles(files);
        }
    }

    public FileToken generateGetFileNoteToken(final String requestId, final UUID fileUuid) {
        return fileNoteTokenService.generateGetRequestFileNoteToken(requestId, fileUuid);
    }

    private void cleanUpUnusedNoteFilesAsync() {

        CompletableFuture.runAsync(fileNoteService::cleanUpUnusedFiles)
            .exceptionally(ex -> {
                log.error(ex);
                return null;
            });
    }

    private RequestNote buildRequestNote(final RequestNoteRequest requestNoteRequest, final AppUser authUser) {

        final Map<UUID, String> fileUuidWithNames = this.getFileUuidWithNames(requestNoteRequest.getFiles());

        return RequestNote.builder()
            .requestId(requestNoteRequest.getRequestId())
            .payload(NotePayload.builder()
                .note(requestNoteRequest.getNote())
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
        		log.warn("Request file note not found for uuid: " + uuid);
        	}
        });
        
        return fileUuidWithNames;
    }
}
