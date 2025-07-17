package uk.gov.pmrv.api.migration.permit.sourcestreams;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SourceStreamRowMapper implements RowMapper<SourceStream>{

    @Override
    public SourceStream mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SourceStream.builder()
                    .etsAccountId(rs.getString("emitterId"))
                    .reference(rs.getString("reference").trim())
                    .description(rs.getString("description").trim())
                    .type(rs.getString("type"))
                    .build();
    }

}