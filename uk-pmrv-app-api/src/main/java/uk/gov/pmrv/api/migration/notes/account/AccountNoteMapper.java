package uk.gov.pmrv.api.migration.notes.account;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountNoteMapper implements RowMapper<AccountNoteRow> {

    @Override
    public AccountNoteRow mapRow(ResultSet rs, int i) throws SQLException {
        return AccountNoteRow.builder()
            .noteId(rs.getString("fldNoteID"))
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .dateCreated(rs.getTimestamp("fldDateCreated").toLocalDateTime())
            .submitter(rs.getString("userFullName"))
            .payload(rs.getString("fldBody"))
            .build();
    }
}
