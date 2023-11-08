package uk.gov.pmrv.api.files.notes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.FileEntity;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@SequenceGenerator(name = "default_file_id_generator", sequenceName = "file_note_seq", allocationSize = 1)
@Table(name = "file_note")
@NamedQuery(
    name = FileNote.NAMED_QUERY_DELETE_NOTE_FILES_BY_STATUS_AND_DATE_BEFORE,
    query = 
        "delete " +
            "from FileNote fileNote " +
            "where fileNote.status =: status " +
            "and fileNote.lastUpdatedOn < :expirationDate"
)
@NamedQuery(
    name = FileNote.NAMED_QUERY_GET_FILE_NAMES_BY_UUID,
    query =
        "select new uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO(fileNote.fileName, fileNote.uuid) " +
            "from FileNote fileNote " +
            "where fileNote.uuid in (:uuids)"
)
@NamedQuery(
    name = FileNote.NAMED_QUERY_UPDATE_NOTE_FILES_STATUS_BY_UUID,
    query =
        "update FileNote fileNote " +
            "set fileNote.status =: status " +
            "where fileNote.uuid in (:uuids)"
)
@NamedQuery(
    name = FileNote.NAMED_QUERY_DELETE_NOTE_FILES_BY_UUID,
    query =
        "delete " +
            "from FileNote fileNote " +
            "where fileNote.uuid in (:uuids)"
)
public class FileNote extends FileEntity {
    
    public static final String NAMED_QUERY_DELETE_NOTE_FILES_BY_STATUS_AND_DATE_BEFORE = "FileNote.deleteNoteFilesByStatusAndDateBefore";
    public static final String NAMED_QUERY_GET_FILE_NAMES_BY_UUID = "FileNote.getFileNamesByUuid";
    public static final String NAMED_QUERY_UPDATE_NOTE_FILES_STATUS_BY_UUID = "FileNote.updateNoteFilesStatusByUuid";
    public static final String NAMED_QUERY_DELETE_NOTE_FILES_BY_UUID = "FileNote.deleteNoteFilesByUuid";

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "request_id")
    private String requestId;
}
