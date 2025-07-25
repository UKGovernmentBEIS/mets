package uk.gov.pmrv.api.migration.permit.type;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsPermitTypeRowMapper implements RowMapper<EtsPermitType> {
    @Override
    public EtsPermitType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsPermitType.builder()
            .etsAccountId(rs.getString("fldEmitterID"))
            .type(rs.getString("permitType"))
            .build();
    }
}
