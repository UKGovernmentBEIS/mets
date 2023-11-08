package uk.gov.pmrv.api.migration.emp.ukets.consolidationnumber;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsEmpVersionMapper implements RowMapper<EtsEmpVersionRow> {

    @Override
    public EtsEmpVersionRow mapRow(ResultSet rs, int i) throws SQLException {

        return EtsEmpVersionRow.builder()
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .consolidationNumber(rs.getInt("empVersion"))
            .build();
    }
}
