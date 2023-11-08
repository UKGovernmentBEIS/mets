package uk.gov.pmrv.api.migration.emp.corsia.datagaps;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EtsEmpDataGapsCorsiaRowMapper implements RowMapper<EtsEmpDataGapsCorsia> {

	@Override
    public EtsEmpDataGapsCorsia mapRow(ResultSet rs, int rowNum) throws SQLException {
        
		return EtsEmpDataGapsCorsia.builder()
            .fldEmitterID(rs.getString("fldEmitterID"))
            .fldEmitterDisplayID(rs.getString("fldEmitterDisplayID"))
            .corsiaDataGapSecondarySource(rs.getString("Corsia_data_gap_secondary_source"))
            .corsiaDataGapHandling(rs.getString("Corsia_data_gap_handling"))
            .corsiaDataGapProcedure(rs.getString("Corsia_data_gap_procedure"))
            .corsiaDataGapOption(rs.getBoolean("Corsia_data_gap_option"))
            .corsiaDataGapExplanation(Boolean.TRUE.equals(rs.getBoolean("Corsia_data_gap_option")) 
            		? rs.getString("Corsia_data_gap_explanation") : null)
            .build();
    }
}
