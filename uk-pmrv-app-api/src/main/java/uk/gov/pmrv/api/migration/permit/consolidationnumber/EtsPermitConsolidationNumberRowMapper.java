package uk.gov.pmrv.api.migration.permit.consolidationnumber;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsPermitConsolidationNumberRowMapper implements RowMapper<EtsPermitConsolidationNumberType> {

    @Override
    public EtsPermitConsolidationNumberType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsPermitConsolidationNumberType.builder()
            .etsAccountId(rs.getString("emitterId"))
            .consolidationNumber(rs.getInt("PermitVersion"))
            .build();
    }
}
