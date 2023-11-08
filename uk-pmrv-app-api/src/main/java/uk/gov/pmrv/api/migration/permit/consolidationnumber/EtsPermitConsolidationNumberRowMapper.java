package uk.gov.pmrv.api.migration.permit.consolidationnumber;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsPermitConsolidationNumberRowMapper implements RowMapper<EtsPermitConsolidationNumberType> {

    @Override
    public EtsPermitConsolidationNumberType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsPermitConsolidationNumberType.builder()
            .etsAccountId(rs.getString("emitterId"))
            .consolidationNumber(rs.getInt("PermitVersion"))
            .build();
    }
}
