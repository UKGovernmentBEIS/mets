package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationco2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsCalculationDefaultValueRowMapper implements RowMapper<EtsCalculationDefaultValue> {

    @Override
    public EtsCalculationDefaultValue mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsCalculationDefaultValue.builder()

            .tierId(rs.getString("tier_id"))
            .etsAccountId(rs.getString("fldEmitterID"))
            .parameter(rs.getString("parameter"))
            .referenceSource(rs.getString("reference_source"))
            .defaultValue(rs.getString("default_value"))
            .build();
    }
}