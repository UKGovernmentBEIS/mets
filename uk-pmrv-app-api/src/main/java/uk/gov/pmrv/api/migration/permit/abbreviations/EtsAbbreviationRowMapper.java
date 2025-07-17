package uk.gov.pmrv.api.migration.permit.abbreviations;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsAbbreviationRowMapper implements RowMapper<EtsAbbreviation> {

    @Override
    public EtsAbbreviation mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return EtsAbbreviation.builder()
            .etsAccountId(resultSet.getString("emitterId"))
            .abbreviation(resultSet.getString("abbreviation"))
            .definition(resultSet.getString("definition"))
            .build();
    }
}
