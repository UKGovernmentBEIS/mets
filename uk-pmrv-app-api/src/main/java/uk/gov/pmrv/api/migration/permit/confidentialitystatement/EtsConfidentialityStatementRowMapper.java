package uk.gov.pmrv.api.migration.permit.confidentialitystatement;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsConfidentialityStatementRowMapper implements RowMapper<EtsConfidentialityStatement> {
    @Override
    public EtsConfidentialityStatement mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return EtsConfidentialityStatement.builder()
            .etsAccountId(resultSet.getString("emitterId"))
            .exist(resultSet.getBoolean("existConfidentialityStatement"))
            .section(resultSet.getString("section"))
            .justification(resultSet.getString("justification"))
            .build();
    }
}
