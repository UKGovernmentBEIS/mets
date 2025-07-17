package uk.gov.pmrv.api.migration.workflow.permitbatchreissue;

import org.springframework.jdbc.core.RowMapper;
import uk.gov.pmrv.api.migration.MigrationHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermitBatchReissueSequenceVOMapper implements RowMapper<PermitBatchReissueSequenceVO> {

	@Override
	public PermitBatchReissueSequenceVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return PermitBatchReissueSequenceVO.builder()
				.ca(MigrationHelper.resolveCompAuth(rs.getString("ca")))
				.latestReissueId(rs.getInt("latestReissueId"))
				.build();
	}

}
