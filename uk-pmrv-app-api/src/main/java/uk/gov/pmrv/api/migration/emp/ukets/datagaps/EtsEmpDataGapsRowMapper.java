package uk.gov.pmrv.api.migration.emp.ukets.datagaps;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmpDataGapsRowMapper implements RowMapper<EtsEmpDataGaps> {

    @Override
    public EtsEmpDataGaps mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsEmpDataGaps.builder()
            .fldEmitterId(rs.getString("fldEmitterId"))
            .fldEmitterDisplayId(rs.getString("fldEmitterDisplayId"))
            .thresholdDataGap(rs.getString("Threshold_data_gap"))
            .secondaryDataGap(rs.getString("Secondary_data_gap"))
            .alternativeMethodUsed(rs.getString("Alternative_method_used"))
            .dataGapMethodology(rs.getString("Data_gap_methodology"))
            .build();
    }
}
