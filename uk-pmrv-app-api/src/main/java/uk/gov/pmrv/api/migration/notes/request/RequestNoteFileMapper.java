package uk.gov.pmrv.api.migration.notes.request;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RequestNoteFileMapper implements RowMapper<RequestNoteFileRow> {

    @Override
    public RequestNoteFileRow mapRow(ResultSet rs, int i) throws SQLException {
        return RequestNoteFileRow.builder()
            .noteId(rs.getString("fldNoteID"))
            .requestId(rs.getString("workflowId"))
            .attachmentId(UUID.fromString(rs.getString("fldNoteAttachmentID")))
            .fileContent(rs.getString("fldFileLocation"))
            .fileName(rs.getString("fldFileName"))
            .build();
    }
}
