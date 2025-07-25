package uk.gov.pmrv.api.migration.notes.request;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestNoteMapper implements RowMapper<RequestNoteRow> {

    @Override
    public RequestNoteRow mapRow(ResultSet rs, int i) throws SQLException {
        return RequestNoteRow.builder()
            .noteId(rs.getString("fldNoteID"))
            .requestId(rs.getString("workflowId"))
            .dateCreated(rs.getTimestamp("fldDateCreated").toLocalDateTime())
            .submitter(rs.getString("userFullName"))
            .payload(rs.getString("fldBody"))
            .build();
    }
}
