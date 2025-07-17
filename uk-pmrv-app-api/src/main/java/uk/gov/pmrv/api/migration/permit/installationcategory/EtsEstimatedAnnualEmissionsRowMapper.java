package uk.gov.pmrv.api.migration.permit.installationcategory;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEstimatedAnnualEmissionsRowMapper implements RowMapper<EtsEstimatedAnnualEmissions>{

    @Override
    public EtsEstimatedAnnualEmissions mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsEstimatedAnnualEmissions.builder()
                    .emitterId(rs.getString("emitterId"))
                    .estimatedAnnualEmission(rs.getString("estimatedAnnualEmission"))
                    .build();
    }

}
