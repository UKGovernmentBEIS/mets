package uk.gov.pmrv.api.migration.emp.common.documents;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmpDocumentRowMapper implements RowMapper<EtsEmpDocument> {

    @Override
    public EtsEmpDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsEmpDocument.builder()
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .id(rs.getString("fldAttachmentID"))
            .etsFilename(rs.getString("fldFileName"))
            .migratedFilename(rs.getString("filename"))
            .build();
    }
}
