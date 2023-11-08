package uk.gov.pmrv.api.files.notes.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.common.repository.FileEntityRepository;
import uk.gov.pmrv.api.files.notes.domain.FileNote;

@Repository
public interface FileNoteRepository extends FileEntityRepository<FileNote, Long> {

    @Transactional
    @Modifying
    @Query(name = FileNote.NAMED_QUERY_DELETE_NOTE_FILES_BY_STATUS_AND_DATE_BEFORE)
    void deleteNoteFilesByStatusAndDateBefore(FileStatus status, LocalDateTime expirationDate);

    @Transactional(readOnly = true)
    @Query(name = FileNote.NAMED_QUERY_GET_FILE_NAMES_BY_UUID)
    List<FileInfoDTO> getFileNamesByUuid(Set<String> uuids);

    @Transactional
    @Modifying
    @Query(name = FileNote.NAMED_QUERY_UPDATE_NOTE_FILES_STATUS_BY_UUID)
    void updateNoteFilesStatusByUuid(Set<String> uuids, FileStatus status);

    @Transactional
    @Modifying
    @Query(name = FileNote.NAMED_QUERY_DELETE_NOTE_FILES_BY_UUID)
    void deleteNoteFilesByUuid(Set<String> uuids);
    
    boolean existsByAccountIdAndUuid(Long accountId, String uuid);

    boolean existsByRequestIdAndUuid(String requestId, String uuid);

    @Transactional(readOnly = true)
    List<FileNote> findByStatus(FileStatus status);
}
