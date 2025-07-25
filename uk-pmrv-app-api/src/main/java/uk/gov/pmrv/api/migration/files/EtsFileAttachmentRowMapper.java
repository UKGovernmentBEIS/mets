package uk.gov.pmrv.api.migration.files;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class EtsFileAttachmentRowMapper implements RowMapper<EtsFileAttachment>{

    @Override
    public EtsFileAttachment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsFileAttachment.builder()
                .etsAccountId(rs.getString("emitterId"))
                .uploadedFileName(rs.getString("uploadedFileName"))
                .storedFileName(rs.getString("storedFileName"))
                .uuid(UUID.randomUUID())
                .build();
    }

}
