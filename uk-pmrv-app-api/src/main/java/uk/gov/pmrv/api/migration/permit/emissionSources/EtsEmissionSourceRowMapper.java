package uk.gov.pmrv.api.migration.permit.emissionSources;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmissionSourceRowMapper implements RowMapper<EtsEmissionSource> {
    @Override
    public EtsEmissionSource mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return EtsEmissionSource.builder()
            .etsAccountId(resultSet.getString("emitterId"))
            .reference(resultSet.getString("reference").trim())
            .description(resultSet.getString("description").trim())
            .build();
    }
}
