package uk.gov.pmrv.api.migration.activitylevelchange;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class ActivityLevelChangeVOMapper implements RowMapper<ActivityLevelChangeVO> {

    @Override
    public ActivityLevelChangeVO mapRow(ResultSet rs, int i) throws SQLException {
        return ActivityLevelChangeVO.builder()
            .emitterId(rs.getString("emitterId"))
            .emitterDisplayId(rs.getString("emitterDisplayId"))
            .endDate(rs.getTimestamp("fldDateUpdated") != null
                ? rs.getTimestamp("fldDateUpdated").toLocalDateTime()
                : null)
            .historyYear(Year.parse(rs.getString("history_year")))
            .subInstallationName(rs.getString("subinstallation_name"))
            .changeType(rs.getString("change_type"))
            .changedActivityLevel(rs.getString("change_activity_level"))
            .comments(rs.getString("comments"))
            .build();
    }
}
