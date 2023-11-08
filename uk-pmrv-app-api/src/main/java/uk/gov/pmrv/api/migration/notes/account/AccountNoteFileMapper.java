package uk.gov.pmrv.api.migration.notes.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class AccountNoteFileMapper implements RowMapper<AccountNoteFileRow> {

    @Override
    public AccountNoteFileRow mapRow(ResultSet rs, int i) throws SQLException {
        return AccountNoteFileRow.builder()
            .noteId(rs.getString("fldNoteID"))
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .attachmentId(UUID.fromString(rs.getString("fldNoteAttachmentID")))
            .fileContent(rs.getString("fldFileLocation"))
            .fileName(rs.getString("fldFileName"))
            .build();
    }
}
