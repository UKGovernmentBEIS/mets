package uk.gov.pmrv.api.migration.permit.installationdesc;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsInstallationDescriptionRowMapper implements RowMapper<EtsInstallationDescription> {

    @Override
    public EtsInstallationDescription mapRow(ResultSet resultSet, int i) throws SQLException {
        return EtsInstallationDescription.builder()
            .etsAccountId(resultSet.getString("emitterId"))
            .mainActivitiesDesc(resultSet.getString("mainActivitiesDesc"))
            .build();
    }
}
