package uk.gov.pmrv.api.workflow.request.core.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.note.NotePayload;
import uk.gov.pmrv.api.common.note.NoteRequest;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.notes.service.FileNoteService;
import uk.gov.pmrv.api.files.notes.service.FileNoteTokenService;
import uk.gov.pmrv.api.token.FileToken;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestNote;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteDto;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteResponse;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestNoteRepository;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestNoteMapper;

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
    public void createNote(final PmrvUser authUser, final RequestNoteRequest requestNoteRequest) {

        final RequestNote requestNote = this.buildRequestNote(requestNoteRequest, authUser);
        requestNoteRepository.save(requestNote);

        final Set<UUID> filesUuids = requestNoteRequest.getFiles();
        fileNoteService.submitFiles(filesUuids);
    }

    @Transactional
    public void updateNote(final Long noteId, final NoteRequest noteRequest, final PmrvUser authUser) {

        final RequestNote requestNote = requestNoteRepository.findById(noteId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        final Set<UUID> previousFiles = requestNote.getPayload().getFiles().keySet();
        final Set<UUID> currentFiles = noteRequest.getFiles();
        final HashSet<UUID> deletedFiles = new HashSet<>(previousFiles);
        deletedFiles.removeAll(currentFiles);
        if (!deletedFiles.isEmpty()) {
            fileNoteService.deleteFiles(deletedFiles);
        }

        final Map<UUID, String> newFileNames = this.getFileNames(currentFiles);

        requestNote.getPayload().setNote(noteRequest.getNote());
        requestNote.getPayload().setFiles(newFileNames);
        requestNote.setSubmitterId(authUser.getUserId());
        requestNote.setSubmitter(authUser.getFirstName() + " " + authUser.getLastName());
        requestNote.setLastUpdatedOn(dateService.getLocalDateTime());
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

    private RequestNote buildRequestNote(final RequestNoteRequest requestNoteRequest, final PmrvUser authUser) {

        final Map<UUID, String> fileNames = this.getFileNames(requestNoteRequest.getFiles());

        return RequestNote.builder()
            .requestId(requestNoteRequest.getRequestId())
            .payload(NotePayload.builder()
                .note(requestNoteRequest.getNote())
                .files(fileNames)
                .build())
            .submitterId(authUser.getUserId())
            .submitter(authUser.getFirstName() + " " + authUser.getLastName())
            .lastUpdatedOn(dateService.getLocalDateTime())
            .build();
    }

    private Map<UUID, String> getFileNames(final Set<UUID> filesUuids) {

        final Map<UUID, String> fileNames = fileNoteService.getFileNames(filesUuids);
        final int filesFound = fileNames.size();
        if (filesFound != filesUuids.size()) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
        return fileNames;
    }
}
