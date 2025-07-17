package uk.gov.pmrv.api.migration.emp.ukets.consolidationnumber;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmpVersionMapper implements RowMapper<EtsEmpVersionRow> {

    @Override
    public EtsEmpVersionRow mapRow(ResultSet rs, int i) throws SQLException {

        return EtsEmpVersionRow.builder()
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .consolidationNumber(rs.getInt("empVersion"))
            .build();
    }
}
