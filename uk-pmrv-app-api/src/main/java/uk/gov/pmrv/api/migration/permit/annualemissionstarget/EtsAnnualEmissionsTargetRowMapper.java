package uk.gov.pmrv.api.migration.permit.annualemissionstarget;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsAnnualEmissionsTargetRowMapper implements RowMapper<EtsAnnualEmissionsTarget> {

    @Override
    public EtsAnnualEmissionsTarget mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsAnnualEmissionsTarget.builder()
            .etsAccountId(rs.getString("fldEmitterID"))
            .monitoringYear(rs.getString("monitoring_year"))
            .emissionsTarget(rs.getString("emissions_target"))
            .build();
    }
}
