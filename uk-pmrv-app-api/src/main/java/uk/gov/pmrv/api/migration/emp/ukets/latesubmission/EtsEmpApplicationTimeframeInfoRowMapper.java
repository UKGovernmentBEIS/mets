package uk.gov.pmrv.api.migration.emp.ukets.latesubmission;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EtsEmpApplicationTimeframeInfoRowMapper implements RowMapper<EtsEmpApplicationTimeframeInfo>{

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	@Override
    public EtsEmpApplicationTimeframeInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		
        return EtsEmpApplicationTimeframeInfo.builder()
        		.fldEmitterId(rs.getString("fldEmitterId"))
                .fldEmitterDisplayId(rs.getString("fldEmitterDisplayId"))
                .fldFirstFlyDate(rs.getTimestamp("fldFirstFlyDate") != null 
                	? rs.getTimestamp("fldFirstFlyDate").toLocalDateTime().toLocalDate() 
                		: null)
                .dateAviationActivityCaptured(rs.getString("Date_aviation_activity_captured") != null 
                	? LocalDate.parse(rs.getString("Date_aviation_activity_captured"), formatter) 
                			: null)
                .submissionLateYes(rs.getString("Submission_late_yes"))
                .submissionLateJustify(rs.getString("Submission_late_justify"))
                .build();
    }
}
