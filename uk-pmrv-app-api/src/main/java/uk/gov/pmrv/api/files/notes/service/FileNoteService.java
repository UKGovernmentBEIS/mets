package uk.gov.pmrv.api.files.notes.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.pmrv.api.files.common.service.FileValidatorService;
import uk.gov.pmrv.api.files.notes.domain.FileNote;
import uk.gov.pmrv.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.files.notes.transform.FileNoteMapper;

@Service
@Validated
@RequiredArgsConstructor
public class FileNoteService {
    
    private static final int DAYS_CUTOFF = 1;
    
    private final DateService dateService;
    private final FileNoteRepository fileNoteRepository;
    private final List<FileValidatorService> fileValidators;
    private final FileNoteMapper fileNoteMapper;

    public void cleanUpUnusedFiles() {
        
        final LocalDateTime expirationDate = dateService.getLocalDateTime().minusDays(DAYS_CUTOFF);
        fileNoteRepository.deleteNoteFilesByStatusAndDateBefore(FileStatus.PENDING, expirationDate);
    }

    public Map<UUID, String> getFileNames(final Set<UUID> uuids) {
        
        return fileNoteRepository.getFileNamesByUuid(uuidsToStrings(uuids)).stream().collect(
            Collectors.toMap(
                f -> UUID.fromString(f.getUuid()), 
                FileInfoDTO::getName
            ));
    }
    public FileUuidDTO uploadAccountFile(PmrvUser authUser, FileDTO fileDTO, Long accountId) throws IOException {

        final FileNote fileNote = this.validateAndCreateFileNote(authUser.getUserId(), fileDTO);
        fileNote.setAccountId(accountId);
        fileNoteRepository.save(fileNote);

        return FileUuidDTO.builder().uuid(fileNote.getUuid()).build();
    }
    
    public FileUuidDTO uploadRequestFile(final PmrvUser authUser, final FileDTO fileDTO, final String requestId) throws IOException {

        final FileNote fileNote = this.validateAndCreateFileNote(authUser.getUserId(), fileDTO);
        fileNote.setRequestId(requestId);
        fileNoteRepository.save(fileNote);

        return FileUuidDTO.builder().uuid(fileNote.getUuid()).build();
    }
    
    public void submitFiles(final Set<UUID> uuids) {
        fileNoteRepository.updateNoteFilesStatusByUuid(uuidsToStrings(uuids), FileStatus.SUBMITTED);
    }
    
    public void deleteFiles(final Set<UUID> uuids) {
        fileNoteRepository.deleteNoteFilesByUuid(uuidsToStrings(uuids));
    }

    private FileNote validateAndCreateFileNote(final String userId, final FileDTO fileDTO) throws IOException {

        fileValidators.forEach(validator -> validator.validate(fileDTO));

        final FileNote fileNote = fileNoteMapper.toFileNote(fileDTO);
        final String uuid = UUID.randomUUID().toString();
        fileNote.setUuid(uuid);
        fileNote.setStatus(FileStatus.PENDING);
        fileNote.setCreatedBy(userId);
        
        return fileNote;
    }
    
    private static Set<String> uuidsToStrings(final Set<UUID> uuids) {
        return uuids.stream().map(UUID::toString).collect(Collectors.toSet());
    }
}
