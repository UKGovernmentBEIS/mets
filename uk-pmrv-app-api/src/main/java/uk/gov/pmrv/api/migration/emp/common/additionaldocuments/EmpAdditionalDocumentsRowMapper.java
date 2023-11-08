package uk.gov.pmrv.api.migration.emp.common.additionaldocuments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

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
