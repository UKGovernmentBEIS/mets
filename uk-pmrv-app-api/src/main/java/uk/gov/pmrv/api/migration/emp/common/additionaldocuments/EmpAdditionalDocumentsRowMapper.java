package uk.gov.pmrv.api.migration.emp.common.additionaldocuments;

import org.springframework.jdbc.core.RowMapper;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EmpAdditionalDocumentsRowMapper implements RowMapper<EtsFileAttachment>{

	@Override
    public EtsFileAttachment mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsFileAttachment.builder()
        		.etsAccountId(rs.getString("fldEmitterId"))
                .uploadedFileName(rs.getString("uploadedFileName"))
                .storedFileName(rs.getString("storedFileName"))
                .uuid(UUID.randomUUID())
                .build();
    }
}
