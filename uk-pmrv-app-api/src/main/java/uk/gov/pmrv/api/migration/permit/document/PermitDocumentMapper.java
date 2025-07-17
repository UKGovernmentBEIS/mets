package uk.gov.pmrv.api.migration.permit.document;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermitDocumentMapper implements RowMapper<PermitDocument> {

    @Override
    public PermitDocument mapRow(ResultSet rs, int i) throws SQLException {

        return PermitDocument.builder()
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .id(rs.getString("fldAttachmentID"))
            .etsFilename(rs.getString("fldFileName"))
            .filename(rs.getString("filename"))
            .build();
    }
}